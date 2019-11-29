package be.hogent.faith.encryption

import be.hogent.faith.domain.models.Event
import be.hogent.faith.storage.encryption.IEncryptedEvent
import be.hogent.faith.storage.encryption.IEventEncryptor
import java.io.File
import java.util.UUID

class EventEncryptor(
    private val encrypter: Encrypter
) : IEventEncryptor {
    override fun encrypt(event: Event): IEncryptedEvent {
        with(event) {
            return EncryptedEvent(
                dateTime = encrypter.encrypt(dateTime),
                title = title?.let { encrypter.encrypt(it) },
                emotionAvatar = emotionAvatar?.let { encrypter.encrypt(it.path) },
                notes = notes?.let { encrypter.encrypt(it) },
                uuid = encrypter.encrypt(uuid.toString())
            )
        }
    }

    override fun decrypt(encryptedEvent: IEncryptedEvent): Event {
        with(encryptedEvent) {
            return Event(
                dateTime = encrypter.decryptLocalDateTime(dateTime),
                title = title?.let { encrypter.decrypt(it) },
                emotionAvatar = emotionAvatar?.let { File(encrypter.decrypt(it)) },
                notes = notes?.let { encrypter.decrypt(it) },
                uuid = UUID.fromString(encrypter.decrypt(uuid))
            )
        }
    }
}
