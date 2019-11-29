package be.hogent.faith.storage.encryption

import be.hogent.faith.domain.models.Event

interface IEventEncryptor {
    fun encrypt(event: Event): IEncryptedEvent
    fun decrypt(encryptedEvent: IEncryptedEvent): Event
}

interface IEncryptedEvent {
    val dateTime: String
    val title: String?
    val emotionAvatar: String?
    val notes: String?
    val uuid: String
}
