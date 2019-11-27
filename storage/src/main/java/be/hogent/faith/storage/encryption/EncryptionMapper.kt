package be.hogent.faith.storage.encryption

import be.hogent.faith.domain.models.Event

class EncryptionMapper(
    private val encrypter: Encrypter
) {
    fun encrypt(event: Event): EncryptedEvent {
        with(event) {
            return EncryptedEvent(
                dateTime = encrypter.encrypt(dateTime),
                title = encrypter.encrypt(title),
                emotionAvatar = encrypter.encrypt(emotionAvatar?.path),
                notes = encrypter.encrypt(notes),
                uuid = uuid
            )
        }
    }

    fun decrypt(encryptedEvent: EncryptedEvent): Event {
        with(encryptedEvent) {
            return Event(
                dateTime = encrypter.decryptLocalDateTime(dateTime),
                title = encrypter.decryptString(title),
                emotionAvatar = encrypter.decryptFile(emotionAvatar),
                notes = encrypter.decryptString(notes),
                uuid = uuid
            )
        }
    }
}