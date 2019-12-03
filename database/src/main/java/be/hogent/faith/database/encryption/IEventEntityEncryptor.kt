package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.EventEntity

interface IEventEntityEncryptor {
    fun encrypt(eventEntity: EventEntity): EncryptedEventEntity
    fun decrypt(encryptedEvent: EncryptedEventEntity): EventEntity
    fun decryptAll(list: List<EncryptedEventEntity>): List<EventEntity>
}

class EncryptedEventEntity(
    val dateTime: String = "",
    val title: String? = "",
    val emotionAvatar: String? = "",
    val notes: String? = "",
    val uuid: String = "",
    val detailEntities: List<EncryptedDetailEntity> = mutableListOf()
)
