package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.encryption.IEventEncryptionService
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
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.threeten.bp.LocalDateTime

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EncryptedEventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEncryptionService(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : IEventEncryptionService, KoinComponent {
    private val detailEncryptionService by inject<DetailEncryptionService>()

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
        dek: KeysetHandle,
        sdek: KeysetHandle
    ): Single<List<EncryptedDetail>> {

        return Observable.fromIterable(event.details)
            .flatMapSingle { detailEncryptionService.encrypt(it, dek, sdek) }
            .toList()
    }

    override fun decryptData(encryptedEvent: EncryptedEvent): Single<Event> {
        return keyEncrypter.decrypt(encryptedEvent.encryptedDEK)
            .flatMap { dek -> decryptEventData(encryptedEvent, dek) }
    }

    private fun decryptEventData(encryptedEvent: EncryptedEvent, dek: KeysetHandle): Single<Event> {
        val dataEncrypter = DataEncrypter(dek)

        return decryptDetailsData(dek, encryptedEvent)
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

    private fun decryptDetailsData(
        dek: KeysetHandle,
        encryptedEvent: EncryptedEvent
    ): Single<List<Detail>> {

        return Observable.fromIterable(encryptedEvent.details)
            .flatMapSingle { detailEncryptionService.decryptData(it, dek) }
            .toList()
    }

    override fun decryptList(encryptedEvents: List<EncryptedEvent>): Single<List<Event>> {
        return Observable.fromIterable(encryptedEvents)
            .flatMapSingle(this::decryptData)
            .toList()
    }

    override fun decryptFiles(encryptedEvent: EncryptedEvent): Completable {
        return keyEncrypter.decrypt(encryptedEvent.encryptedStreamingDEK)
            .flatMapCompletable { sdek ->
                Completable.merge {
                    Observable.fromIterable(encryptedEvent.details)
                        .map { detail -> detailEncryptionService.decryptDetailFiles(detail, sdek) }
                }
            }
    }
}
