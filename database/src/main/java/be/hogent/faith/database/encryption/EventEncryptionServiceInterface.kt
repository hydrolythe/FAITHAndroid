package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import io.reactivex.Single

interface EventEncryptionServiceInterface {
    /**
     * Encrypts an [EncryptedEventEntity] and all data belonging to that event.
     * The encrypted data is stored in the device's permanent storage.
     *
     * @param event used to get the original details so they can be encrypted as well
     */
    fun encrypt(event: Event): Single<EncryptedEvent>

    fun decrypt(encryptedEvent: EncryptedEvent): Single<Event>
}
