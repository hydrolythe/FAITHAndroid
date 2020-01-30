package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.zipWith
import org.threeten.bp.LocalDateTime

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EncryptedEventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEncryptionService(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : EventEncryptionServiceInterface {

    override fun encrypt(event: Event): Single<EncryptedEvent> {
        val dataKeysetHandle = keyGenerator.generateKeysetHandle()
        val streamingKeySetHandle = keyGenerator.generateStreamingKeysetHandle()

        return encryptEvent(event, dataKeysetHandle, streamingKeySetHandle)
    }

    private fun encryptEvent(
        event: Event,
        keysetHandle: KeysetHandle,
        streamingKeySetHandle: KeysetHandle
    ): Single<EncryptedEvent> {
        val dataEncrypter = DataEncrypter(keysetHandle)
        val encryptedDetails = encryptDetails(event, keysetHandle, streamingKeySetHandle)

        // Encrypt the DEKs so it can be placed next to the data it encrypted in the EncryptedEventEntity
        // TODO: maybe wrap these two into 1 key object so they can be encrypted together
        val encryptedDEK = keyEncrypter.encrypt(keysetHandle)
        val encryptedStreamingDEK = keyEncrypter.encrypt(streamingKeySetHandle)

        return Singles.zip(
            encryptedDetails, encryptedDEK, encryptedStreamingDEK
        ) { details: List<EncryptedDetail>, dek: String, sdek: String ->
            EncryptedEvent(
                dateTime = dataEncrypter.encrypt(event.dateTime.toString()),
                title = dataEncrypter.encrypt(event.title!!),
                emotionAvatar = event.emotionAvatar,
                notes = event.notes?.let { dataEncrypter.encrypt(it) },
                uuid = event.uuid,
                details = details,
                encryptedDEK = dek,
                encryptedStreamingDEK = sdek
            )
        }
    }

    private fun encryptDetails(
        event: Event,
        keysetHandle: KeysetHandle,
        streamingKeySetHandle: KeysetHandle
    ): Single<List<EncryptedDetail>> {
        val fileEncrypter = FileEncrypter(streamingKeySetHandle)
        val detailEntityEncrypter = DetailEncryptionService(DataEncrypter((keysetHandle)))

        return encryptDetailFiles(event, fileEncrypter)
            .andThen(encryptDetailData(event, detailEntityEncrypter))
    }

    private fun encryptDetailData(
        event: Event,
        detailEntityEncrypter: DetailEncryptionService
    ): Single<List<EncryptedDetail>> {
        return Observable.fromIterable(event.details)
            .map(detailEntityEncrypter::encrypt)
            .toList()
    }

    private fun encryptDetailFiles(event: Event, fileEncrypter: FileEncrypter): Completable {
        return Observable.fromIterable(event.details)
            .map(Detail::file)
            .flatMapCompletable(fileEncrypter::encrypt)
    }

    override fun decrypt(encryptedEvent: EncryptedEvent): Single<Event> {
        val dek = keyEncrypter.decrypt(encryptedEvent.encryptedDEK)
        val streamingDEK = keyEncrypter.decrypt(encryptedEvent.encryptedStreamingDEK)

        // TODO: checken of blockingGet goeie aanpak is
        return dek.zipWith(streamingDEK) { dek, sdek ->
            decryptEvent(
                encryptedEvent,
                dek,
                sdek
            ).blockingGet()
        }
    }

    private fun decryptEvent(
        encryptedEvent: EncryptedEvent,
        dek: KeysetHandle,
        streamingDEK: KeysetHandle
    ): Single<Event> {
        val dataEncrypter = DataEncrypter(dek)
        val fileEncrypter = FileEncrypter(streamingDEK)

        return decryptDetails(dataEncrypter, encryptedEvent, fileEncrypter)
            .map { details ->
                Event(
                    dateTime = LocalDateTime.parse(dataEncrypter.decrypt(encryptedEvent.dateTime)),
                    title = encryptedEvent.title.let { dataEncrypter.decrypt(it) },
                    emotionAvatar = encryptedEvent.emotionAvatar,
                    notes = encryptedEvent.notes?.let { dataEncrypter.decrypt(it) },
                    uuid = encryptedEvent.uuid,
                    details = details
                )
            }
    }

    private fun decryptDetails(
        dataEncrypter: DataEncrypter,
        encryptedEvent: EncryptedEvent,
        fileEncrypter: FileEncrypter
    ): Single<List<Detail>> {
        val detailEntityEncrypter = DetailEncryptionService(dataEncrypter)

        // Decrypt detail files
        return decryptDetailFiles(encryptedEvent, fileEncrypter).andThen(
            decryptDetailData(encryptedEvent, detailEntityEncrypter)
        )
    }

    private fun decryptDetailData(
        encryptedEvent: EncryptedEvent,
        detailEntityEncrypter: DetailEncryptionService
    ): Single<List<Detail>> {
        return Observable.fromIterable(encryptedEvent.details)
            .map(detailEntityEncrypter::decrypt)
            .toList()
    }

    private fun decryptDetailFiles(
        encryptedEvent: EncryptedEvent,
        fileEncrypter: FileEncrypter
    ): Completable {
        return Observable.fromIterable(encryptedEvent.details)
            .map(EncryptedDetail::file)
            .flatMapCompletable(fileEncrypter::decrypt)
    }

    override fun decryptList(encryptedEvents: List<EncryptedEvent>): Single<List<Event>> {
        return Observable.fromIterable(encryptedEvents)
            .flatMapSingle(this::decrypt)
            .toList()
    }
}
