package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetailEntity
import be.hogent.faith.database.encryption.EncryptedEventEntity
import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import com.google.crypto.tink.KeysetHandle
import java.io.File

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EncryptedEventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEncryptionServiceInterface(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : EventEncryptionServiceInterface {

    override fun encrypt(event: Event): EncryptedEvent {
        val dataKeysetHandle = keyGenerator.generateKeysetHandle()
        val streamingKeySetHandle = keyGenerator.generateStreamingKeysetHandle()

        return encryptEntity(eventEntity, dataKeysetHandle, streamingKeySetHandle)
    }

    private fun encryptEntity(
        eventEntity: EncryptedEventEntity,
        keysetHandle: KeysetHandle,
        streamingKeySetHandle: KeysetHandle
    ): EncryptedEventEntity {
        val dataEncrypter = DataEncrypter(keysetHandle)
        val encryptedDetails = encryptDetails(eventEntity, keysetHandle, streamingKeySetHandle)

        // Encrypt the DEKs so it can be placed next to the data it encrypted in the EncryptedEventEntity
        // TODO: maybe wrap these two into 1 key object so they can be encrypted together
        val encryptedDEK = keyEncrypter.encrypt(keysetHandle)
        val encryptedStreamingDEK = keyEncrypter.encrypt(streamingKeySetHandle)

        with(eventEntity) {
            return EncryptedEventEntity(
                dateTime = dataEncrypter.encrypt(dateTime),
                title = title?.let { dataEncrypter.encrypt(it) },
                emotionAvatar = emotionAvatar?.let { dataEncrypter.encrypt(it) },
                notes = notes?.let { dataEncrypter.encrypt(it) },
                uuid = uuid,
                detailEntities = encryptedDetails,
                encryptedDEK = encryptedDEK,
                encryptedStreamingDEK = encryptedStreamingDEK
            )
        }
    }

    private fun encryptDetails(
        eventEntity: EncryptedEventEntity,
        keysetHandle: KeysetHandle,
        streamingKeySetHandle: KeysetHandle
    ): List<EncryptedDetailEntity> {
        val fileEncrypter = FileEncrypter(streamingKeySetHandle)
        val detailEntityEncrypter = DetailEncryptionService(DataEncrypter((keysetHandle)))

        encryptDetailFiles(eventEntity, fileEncrypter)

        return encryptDetailData(eventEntity, detailEntityEncrypter)
    }

    private fun encryptDetailData(
        eventEntity: EncryptedEventEntity,
        detailEntityEncrypter: DetailEncryptionService
    ): List<EncryptedDetailEntity> {
        return eventEntity.details
            .map { detailEntityEncrypter.encrypt(it) }
    }

    private fun encryptDetailFiles(
        eventEntity: EncryptedEventEntity,
        fileEncrypter: FileEncrypter
    ) {
        eventEntity.details
            .map { detailEntity: EncryptedDetailEntity -> File(detailEntity.file) }
            .forEach { fileEncrypter.encrypt(it) }
    }

    override fun decrypt(encryptedEvent: EncryptedEventEntity): EncryptedEventEntity {
        val dek = keyEncrypter.decrypt(encryptedEvent.encryptedDEK)
        val streamingDEK = keyEncrypter.decrypt(encryptedEvent.encryptedStreamingDEK)
        return decryptEventEntity(encryptedEvent, dek, streamingDEK)
    }

    private fun decryptEventEntity(
        encryptedEvent: EncryptedEventEntity,
        dek: KeysetHandle,
        streamingDEK: KeysetHandle
    ): EncryptedEventEntity {
        val dataEncrypter = DataEncrypter(dek)
        val fileEncrypter = FileEncrypter(streamingDEK)

        with(encryptedEvent) {
            val details = decryptDetails(dataEncrypter, encryptedEvent, fileEncrypter)
            return EventEntity(
                dateTime = dataEncrypter.decrypt(dateTime),
                title = title?.let { dataEncrypter.decrypt(it) },
                emotionAvatar = emotionAvatar?.let { dataEncrypter.decrypt(it) },
                notes = notes?.let { dataEncrypter.decrypt(it) },
                uuid = uuid,
                details = details
            )
        }
    }

    private fun decryptDetails(
        dataEncrypter: DataEncrypter,
        encryptedEvent: EncryptedEventEntity,
        fileEncrypter: FileEncrypter
    ): List<EncryptedDetailEntity> {
        val detailEntityEncrypter = DetailEncryptionService(dataEncrypter)

        encryptedEvent.detailEntities.map {
            File(it.file)
        }.forEach {
            fileEncrypter.decrypt(it)
        }
        return encryptedEvent.detailEntities
            .map { detailEntityEncrypter.decrypt(it) }
            .toList()
    }

    override fun decryptAll(list: List<EncryptedEventEntity>): List<EncryptedEventEntity> {
        return list
            .map { decrypt(it) }
            .toList()
    }
}
