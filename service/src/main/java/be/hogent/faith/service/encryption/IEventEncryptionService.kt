package be.hogent.faith.service.encryption

import be.hogent.faith.domain.models.Event
import io.reactivex.Completable
import io.reactivex.Single

interface IEventEncryptionService {
    /**
     * Encrypts the [event]s data and  files. Does the same  for the details in the event.
     *
     * @return an [EncryptedEvent] whose files are encrypted, and whose details are also encrypted.
     */
    fun encrypt(event: Event): Single<EncryptedEvent>

    /**
     * Decrypts the [encryptedEvent]s data, but not its files.
     */
    fun decryptData(encryptedEvent: EncryptedEvent): Single<Event>

    /**
     * Decrypts the files belonging to an event. After calling this, the paths inside the event
     * will have been updated.
     */
    fun decryptFiles(encryptedEvent: EncryptedEvent): Completable
}
