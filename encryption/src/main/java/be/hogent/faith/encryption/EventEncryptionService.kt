package be.hogent.faith.encryption

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.service.encryption.IEventEncryptionService
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.zipWith
import org.koin.core.KoinComponent
import org.threeten.bp.LocalDateTime
import java.io.File

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EncryptedEventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEncryptionService(
    private val detailEncryptionService: DetailEncryptionService,
    private val fileEncryptionService: FileEncryptionService,
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : IEventEncryptionService, KoinComponent {

    override fun encrypt(event: Event): Single<EncryptedEvent> {
        val dataKey = keyGenerator.generateKeysetHandle()
        val streamingDataKey = keyGenerator.generateStreamingKeysetHandle()

        // TODO: find a way to handle a null emotionAvatar on the Event
        var encryptedEmotionAvatar: Single<File> = Single.just(File(""))
        if (event.emotionAvatar != null) {
            encryptedEmotionAvatar =
                fileEncryptionService.encrypt(event.emotionAvatar!!, streamingDataKey)
        }
        val encryptedDetails = encryptDetails(event, dataKey, streamingDataKey)

        return Singles.zip(
            encryptEventData(event, dataKey, streamingDataKey),
            encryptedDetails,
            encryptedEmotionAvatar
        ) { encryptedEvent, details, emotionAvatar ->
            encryptedEvent.details = details
            encryptedEvent.emotionAvatar = emotionAvatar
            encryptedEvent
        }
    }

    private fun encryptEventData(
        event: Event,
        dataKey: KeysetHandle,
        streamingKey: KeysetHandle
    ): Single<EncryptedEvent> {
        val encryptedDEK = keyEncrypter.encrypt(dataKey)
        val encryptedStreamingDEK = keyEncrypter.encrypt(streamingKey)

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
    }

    private fun decryptDetailsData(
        dek: KeysetHandle,
        encryptedEvent: EncryptedEvent
    ): Single<List<Detail>> {
        return Observable.fromIterable(encryptedEvent.details)
            .flatMapSingle { detailEncryptionService.decryptData(it, dek) }
            .toList()
    }

    override fun decryptFiles(encryptedEvent: EncryptedEvent): Completable {
        // TODO: emotionavatar decrypten
        return keyEncrypter.decrypt(encryptedEvent.encryptedStreamingDEK)
            .flatMapCompletable { sdek ->
                Completable.merge {
                    Observable.fromIterable(encryptedEvent.details)
                        .map { detail -> detailEncryptionService.decryptDetailFiles(detail, sdek) }
                }
            }
    }
}
