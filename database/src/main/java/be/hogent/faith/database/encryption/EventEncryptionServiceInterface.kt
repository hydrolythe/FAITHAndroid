package be.hogent.faith.database.encryption

import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event

interface EventEncryptionServiceInterface {
    /**
     * Encrypts an [EncryptedEventEntity] and all data belonging to that event.
     * The encrypted data is stored in the device's permanent storage.
     *
     * @param event used to get the original details so they can be encrypted as well
     */
    fun encrypt(event: Event): EncryptedEventInterface

    fun decrypt(encryptedEvent: EncryptedEventInterface): Event
    fun decryptAll(list: List<EncryptedEventInterface>): List<Event>
}
