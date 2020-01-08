package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedDetailEntity
import be.hogent.faith.database.encryption.EncryptedEventEntity
import be.hogent.faith.database.encryption.IEventEntityEncrypter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle

/**
 * @param keyGenerator will be used to generate the DEK that will be used when encrypting the [EventEntity].
 * @param keyEncrypter will be used to do the encrypting with the DEK
 */
class EventEntityEncrypter(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : IEventEntityEncrypter {

    override fun encrypt(eventEntity: EventEntity): EncryptedEventEntity {
        val keysetHandle = keyGenerator.generateKeysetHandle()
        val dataEncryptionKey = keysetHandle.getPrimitive(Aead::class.java)
        val dataEncrypter = DataEncrypter(dataEncryptionKey)

        return encryptEntity(eventEntity, keysetHandle, dataEncrypter)
    }

    private fun encryptEntity(
        eventEntity: EventEntity,
        keysetHandle: KeysetHandle,
        dataEncrypter: DataEncrypter
    ): EncryptedEventEntity {
        val encryptedDetails = encryptDetails(eventEntity, DetailEntityEncrypter(dataEncrypter))

        // Encrypt the DEK so it can be placed next to the data it encrypted in the EncryptedEventEntity
        val encryptedDEK = keyEncrypter.encrypt(keysetHandle)

        with(eventEntity) {
            return EncryptedEventEntity(
                dateTime = dataEncrypter.encrypt(dateTime),
                title = title?.let { dataEncrypter.encrypt(it) },
                emotionAvatar = emotionAvatar?.let { dataEncrypter.encrypt(it) },
                notes = notes?.let { dataEncrypter.encrypt(it) },
                uuid = uuid,
                detailEntities = encryptedDetails,
                encryptedDEK = encryptedDEK
            )
        }
    }

    private fun encryptDetails(
        eventEntity: EventEntity,
        detailEntityEncrypter: DetailEntityEncrypter
    ): List<EncryptedDetailEntity> {
        return eventEntity.details
            .map { detailEntityEncrypter.encrypt(it) }
            .toList()
    }

    override fun decrypt(encryptedEvent: EncryptedEventEntity): EventEntity {
        val dataEncryptionKey = decryptDEK(encryptedEvent)
        return decryptEventEntity(encryptedEvent, dataEncryptionKey)
    }

    private fun decryptDEK(encryptedEvent: EncryptedEventEntity): Aead {
        val keysetHandle = keyEncrypter.decrypt(encryptedEvent.encryptedDEK)
        return keysetHandle.getPrimitive(Aead::class.java)
    }

    private fun decryptEventEntity(
        encryptedEvent: EncryptedEventEntity,
        dataEncryptionKey: Aead
    ): EventEntity {
        val dataEncrypter = DataEncrypter(dataEncryptionKey)

        with(encryptedEvent) {
            val details = decryptDetails(dataEncrypter, encryptedEvent)
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
        encryptedEvent: EncryptedEventEntity
    ): List<DetailEntity> {
        val detailEntityEncrypter = DetailEntityEncrypter(dataEncrypter)
        return encryptedEvent.detailEntities
            .map { detailEntityEncrypter.decrypt(it) }
            .toList()
    }

    override fun decryptAll(list: List<EncryptedEventEntity>): List<EventEntity> {
        return list
            .map { decrypt(it) }
            .toList()
    }
}
