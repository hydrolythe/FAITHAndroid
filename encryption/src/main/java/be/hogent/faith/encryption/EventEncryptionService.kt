package be.hogent.faith.encryption

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.storage.StoragePathProvider
import com.google.crypto.tink.KeysetHandle
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.zipWith
import org.koin.core.KoinComponent
import org.threeten.bp.LocalDateTime
import timber.log.Timber

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EncryptedEventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEncryptionService(
    private val detailEncryptionService: DetailEncryptionService,
    private val fileEncryptionService: FileEncryptionService,
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter,
    private val pathProvider: StoragePathProvider
) : IEventEncryptionService, KoinComponent {

    override fun encrypt(event: Event): Single<EncryptedEvent> {
        val dataKey = keyGenerator.generateKeysetHandle()
        val streamingDataKey = keyGenerator.generateStreamingKeysetHandle()

        val encryptedDetails = encryptDetails(event, dataKey, streamingDataKey)
            .doOnSuccess { Timber.i("Encrypted details for event ${event.uuid}") }

        if (event.emotionAvatar != null) {
            val encryptedEmotionAvatar =
                fileEncryptionService
                    .encrypt(event.emotionAvatar!!, streamingDataKey)
                    .doOnSuccess { Timber.i("Encrypted emotionAvatar for event ${event.uuid}") }
            return Singles.zip(
                encryptEventData(event, dataKey, streamingDataKey),
                encryptedDetails,
                encryptedEmotionAvatar
            ) { encryptedEvent, details, emotionAvatar ->
                encryptedEvent.details = details
                encryptedEvent.emotionAvatar = emotionAvatar
                encryptedEvent
            }
        } else {
            return encryptEventData(
                event,
                dataKey,
                streamingDataKey
            ).zipWith(encryptedDetails) { encryptedEvent, details ->
                encryptedEvent.details = details
                encryptedEvent
            }
        }
    }

    private fun encryptEventData(
        event: Event,
        dataKey: KeysetHandle,
        streamingKey: KeysetHandle
    ): Single<EncryptedEvent> {
        val encryptedDEK = keyEncrypter.encrypt(dataKey)
            .doOnSuccess { Timber.i("Encrypted dek for ${event.uuid}") }
        val encryptedStreamingDEK = keyEncrypter.encrypt(streamingKey)
            .doOnSuccess { Timber.i("Encrypted sdek for ${event.uuid}") }

        return encryptedDEK.zipWith(encryptedStreamingDEK) { encryptedDek, encryptedSdek ->
            with(DataEncrypter(dataKey)) {
                EncryptedEvent(
                    dateTime = encrypt(event.dateTime.toString()),
                    title = encrypt(event.title!!),
                    emotionAvatar = event.emotionAvatar,
                    notes = event.notes?.let { encrypt(it) },
                    uuid = event.uuid,
                    encryptedDEK = encryptedDek,
                    encryptedStreamingDEK = encryptedSdek
                )
            }
        }
            .doOnSuccess { Timber.i("Encrypted data for event ${event.uuid}") }
    }

    private fun encryptDetails(
        event: Event,
        dek: KeysetHandle,
        sdek: KeysetHandle
    ): Single<List<EncryptedDetail>> {
        return Observable
            .fromIterable(event.details)
            .flatMapSingle { detailEncryptionService.encrypt(it, dek, sdek) }
            .toList()
    }

    override fun decryptData(encryptedEvent: EncryptedEvent): Single<Event> {
        return keyEncrypter
            .decrypt(encryptedEvent.encryptedDEK)
            .doOnSuccess { Timber.i("decrypted dek for ${encryptedEvent.uuid}") }
            .flatMap { dek -> decryptEventData(encryptedEvent, dek) }
    }

    private fun decryptEventData(encryptedEvent: EncryptedEvent, dek: KeysetHandle): Single<Event> {
        return decryptDetailsData(dek, encryptedEvent)
            .map { details ->
                with(DataEncrypter(dek)) {
                    Event(
                        dateTime = LocalDateTime.parse(decrypt(encryptedEvent.dateTime)),
                        title = encryptedEvent.title.let { decrypt(it) },
                        emotionAvatar = encryptedEvent.emotionAvatar,
                        notes = encryptedEvent.notes?.let { decrypt(it) },
                        uuid = encryptedEvent.uuid
                    ).apply {
                        details.forEach(::addDetail)
                    }
                }
            }
            .doOnSuccess { Timber.i("Decrypted data for event ${encryptedEvent.uuid}") }
    }

    private fun decryptDetailsData(
        dek: KeysetHandle,
        encryptedEvent: EncryptedEvent
    ): Single<List<Detail>> {
        return Observable.fromIterable(encryptedEvent.details)
            .flatMapSingle { detailEncryptionService.decryptData(it, dek) }
            .toList()
            .doOnSuccess { Timber.i("Decrypted details data for event ${encryptedEvent.uuid}") }
    }

    override fun decryptFiles(encryptedEvent: EncryptedEvent): Completable {
        return keyEncrypter.decrypt(encryptedEvent.encryptedStreamingDEK)
            .doOnSuccess { Timber.i("decrypted sdek for event ${encryptedEvent.uuid}") }
            .flatMapCompletable {
                Completable.mergeArray(
                    decryptEmotionAvatar(encryptedEvent, it),
                    decryptDetailFiles(encryptedEvent, it)
                )
            }
    }

    private fun decryptEmotionAvatar(
        encryptedEvent: EncryptedEvent,
        sdek: KeysetHandle
    ): Completable {
        return Observable.just(encryptedEvent)
            .flatMapCompletable { event ->
                if (event.emotionAvatar == null) {
                    Completable
                        .complete()
                        .doOnComplete { Timber.i("No emotionAvatar to decrypt for event ${encryptedEvent.uuid}") }
                } else {
                    val destinationFile =
                        with(pathProvider) { temporaryStorage(emotionAvatarPath(encryptedEvent)) }
                    fileEncryptionService.decrypt(
                        // Where the file of event.emotionAvatar points to is not relevant, as that is just where it was pointing
                        // the moment it was saved to the database.
                        // The encrypted version of the file should be in localstorage at this time, so we assume that path.
                        (if (event.emotionAvatar != null && event.emotionAvatar!!.exists()) {
                            event.emotionAvatar!!
                        } else {
                            pathProvider.localStorage(pathProvider.emotionAvatarPath(encryptedEvent))
                        }),
                        sdek,
                        destinationFile
                    )
                        .andThen(Completable.defer {
                            Completable.fromAction {
                                encryptedEvent.emotionAvatar = destinationFile
                            }
                        })
                }
            }
    }

    private fun decryptDetailFiles(
        encryptedEvent: EncryptedEvent,
        sdek: KeysetHandle
    ): Completable {
        return Completable
            .merge(
                encryptedEvent.details.map { detail ->
                    // Where the file of the detail points to is not relevant, as that is just where it was pointing
                    // the moment it was saved to the database.
                    // The encrypted version of the file should be in localstorage at this time, so we assume that path.
                    detailEncryptionService.decryptDetailFile(
                        detail,
                        sdek,
                        with(pathProvider) { temporaryStorage(detailPath(detail, encryptedEvent)) })
                        .andThen(Completable.fromAction {
                            detail.file = with(pathProvider) { temporaryStorage(detailPath(detail, encryptedEvent)) }
                        })
                }
            )
            .doOnComplete { Timber.i("Decrypted all details' files for event ${encryptedEvent.uuid}") }
    }
}
