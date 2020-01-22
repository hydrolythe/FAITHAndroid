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
import org.threeten.bp.LocalDateTime

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EncryptedEventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEncryptionService(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : EventEncryptionServiceInterface {

    override fun encrypt(event: Event): EncryptedEvent {
        val dataKeysetHandle = keyGenerator.generateKeysetHandle()
        val streamingKeySetHandle = keyGenerator.generateStreamingKeysetHandle()

        return encryptEvent(event, dataKeysetHandle, streamingKeySetHandle)
    }

    private fun encryptEvent(
        event: Event,
        keysetHandle: KeysetHandle,
        streamingKeySetHandle: KeysetHandle
    ): EncryptedEvent {
        val dataEncrypter = DataEncrypter(keysetHandle)
        val encryptedDetails = encryptDetails(event, keysetHandle, streamingKeySetHandle)

        // Encrypt the DEKs so it can be placed next to the data it encrypted in the EncryptedEventEntity
        // TODO: maybe wrap these two into 1 key object so they can be encrypted together
        val encryptedDEK = keyEncrypter.encrypt(keysetHandle)
        val encryptedStreamingDEK = keyEncrypter.encrypt(streamingKeySetHandle)

        with(event) {
            return EncryptedEvent(
                dateTime = dataEncrypter.encrypt(dateTime.toString()),
                title = dataEncrypter.encrypt(title!!),
                emotionAvatar = emotionAvatar,
                notes = notes?.let { dataEncrypter.encrypt(it) },
                uuid = uuid,
                details = encryptedDetails,
                encryptedDEK = encryptedDEK,
                encryptedStreamingDEK = encryptedStreamingDEK
            )
        }
    }

    private fun encryptDetails(
        event: Event,
        keysetHandle: KeysetHandle,
        streamingKeySetHandle: KeysetHandle
    ): List<EncryptedDetail> {
        val fileEncrypter = FileEncrypter(streamingKeySetHandle)
        val detailEntityEncrypter = DetailEncryptionService(DataEncrypter((keysetHandle)))

        encryptDetailFiles(event, fileEncrypter)

        return encryptDetailData(event, detailEntityEncrypter)
    }

    private fun encryptDetailData(
        event: Event,
        detailEntityEncrypter: DetailEncryptionService
    ): List<EncryptedDetail> {
        return event.details
            .map { detailEntityEncrypter.encrypt(it) }
    }

    private fun encryptDetailFiles(event: Event, fileEncrypter: FileEncrypter) {
        event.details
            .map(Detail::file)
            .forEach { fileEncrypter.encrypt(it) }
    }

    override fun decrypt(encryptedEvent: EncryptedEvent): Event {
        val dek = keyEncrypter.decrypt(encryptedEvent.encryptedDEK)
        val streamingDEK = keyEncrypter.decrypt(encryptedEvent.encryptedStreamingDEK)
        return decryptEvent(encryptedEvent, dek, streamingDEK)
    }

    private fun decryptEvent(
        encryptedEvent: EncryptedEvent,
        dek: KeysetHandle,
        streamingDEK: KeysetHandle
    ): Event {
        val dataEncrypter = DataEncrypter(dek)
        val fileEncrypter = FileEncrypter(streamingDEK)

        val details = decryptDetails(dataEncrypter, encryptedEvent, fileEncrypter)
        with(encryptedEvent) {
            val event = Event(
                dateTime = LocalDateTime.parse(dataEncrypter.decrypt(dateTime)),
                title = title.let { dataEncrypter.decrypt(it) },
                emotionAvatar = emotionAvatar,
                notes = notes?.let { dataEncrypter.decrypt(it) },
                uuid = uuid
            )
            details.forEach { event.addDetail(it) }
            return event
        }
    }

    private fun decryptDetails(
        dataEncrypter: DataEncrypter,
        encryptedEvent: EncryptedEvent,
        fileEncrypter: FileEncrypter
    ): List<Detail> {
        val detailEntityEncrypter = DetailEncryptionService(dataEncrypter)

        // Decrypt detail files
        encryptedEvent.details
            .map { it.file }
            .forEach { fileEncrypter.decrypt(it) }

        // Decrypt detail data
        return encryptedEvent.details
            .map { detailEntityEncrypter.decrypt(it) }
            .toList()
    }

    override fun decryptAll(list: List<EncryptedEvent>): List<Event> {
        return list
            .map { decrypt(it) }
            .toList()
    }
}
