package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedEventEntity
import be.hogent.faith.database.encryption.IDetailEntityEncrypter
import be.hogent.faith.database.encryption.IEventEntityEncryptor
import be.hogent.faith.database.models.EventEntity

class EventEntityEncryptor(
    private val encrypter: Encrypter,
    private val detailEntityEncrypter: IDetailEntityEncrypter
) : IEventEntityEncryptor {

    override fun encrypt(eventEntity: EventEntity): EncryptedEventEntity {
        with(eventEntity) {
            val encryptedDetails = eventEntity.details
                .map { detailEntityEncrypter.encrypt(it) }
                .toList()
            return EncryptedEventEntity(
                dateTime = encrypter.encrypt(dateTime),
                title = title?.let { encrypter.encrypt(it) },
                emotionAvatar = emotionAvatar?.let { encrypter.encrypt(it) },
                notes = notes?.let { encrypter.encrypt(it) },
                uuid = uuid,
                detailEntities = encryptedDetails
            )
        }
    }

    override fun decrypt(encryptedEvent: EncryptedEventEntity): EventEntity {
        with(encryptedEvent) {
            val details = encryptedEvent.detailEntities
                .map { detailEntityEncrypter.decrypt(it) }
                .toList()
            return EventEntity(
                dateTime = encrypter.decrypt(dateTime),
                title = title?.let { encrypter.decrypt(it) },
                emotionAvatar = emotionAvatar?.let { encrypter.decrypt(it) },
                notes = notes?.let { encrypter.decrypt(it) },
                uuid = uuid,
                details = details
            )
        }
    }

    override fun decryptAll(list: List<EncryptedEventEntity>): List<EventEntity> {
        return list
            .map { decrypt(it) }
            .toList()
    }
}
