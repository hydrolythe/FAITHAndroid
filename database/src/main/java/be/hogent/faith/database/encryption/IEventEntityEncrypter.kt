package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event

interface IEventEntityEncrypter {
    /**
     * Encrypts an [EventEntity] and all data belonging to that event.
     * The encrypted data is stored in the device's permanent storage.
     *
     * @param originalEvent used to get the original details so they can be encrypted as well
     */
    fun encrypt(originalEvent: Event, eventEntity: EventEntity): EncryptedEventEntity

    fun decrypt(encryptedEvent: EncryptedEventEntity): EventEntity
    fun decryptAll(list: List<EncryptedEventEntity>): List<EventEntity>
}

class EncryptedEventEntity(
    val dateTime: String = "",
    val title: String? = "",
    val emotionAvatar: String? = "",
    val notes: String? = "",
    val uuid: String = "",
    val detailEntities: List<EncryptedDetailEntity> = mutableListOf(),
    val encryptedDEK: String = "",
    val encryptedStreamingDEK: String = ""
)
