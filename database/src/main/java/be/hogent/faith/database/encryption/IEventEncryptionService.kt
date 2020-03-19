package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import io.reactivex.Completable
import io.reactivex.Single

interface IEventEncryptionService {
    /**
     * Encrypts an [EncryptedEventEntity] and all data belonging to that event.
     * The encrypted data is stored in the device's permanent storage.
     *
     * @param event used to get the original details so they can be encrypted as well
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

    fun decryptList(encryptedEvents: List<EncryptedEvent>): Single<List<Event>>
}
