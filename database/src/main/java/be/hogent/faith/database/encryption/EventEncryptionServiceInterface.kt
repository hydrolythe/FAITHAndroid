package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import io.reactivex.Completable
import io.reactivex.Single

interface EventEncryptionServiceInterface {
    /**
     * Encrypts an [EncryptedEventEntity] and all data belonging to that event.
     * The encrypted data is stored in the device's permanent storage.
     *
     * @param event used to get the original details so they can be encrypted as well
     */
    fun encrypt(event: Event): Single<EncryptedEvent>

    /**
     * Decrypts the given [encryptedEvent], but not the files belonging to that event.
     * Use [EventEncryptionServiceInterface.decryptEventFiles] to decrypt these.
     */
    fun decryptEventData(encryptedEvent: EncryptedEvent): Single<Event>

    /**
     * Decrypts the files belonging to the given [event].
     * These files must be already downloaded to local storage!
     */
    fun decryptEventFiles(encryptedEvent: EncryptedEvent): Completable

    fun decryptList(encryptedEvents: List<EncryptedEvent>): Single<List<Event>>
}
