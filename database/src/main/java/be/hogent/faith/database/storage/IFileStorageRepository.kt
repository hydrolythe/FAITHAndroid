package be.hogent.faith.database.storage

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.models.EncryptedEventEntity
import io.reactivex.Single
import java.util.UUID

interface IFileStorageRepository {

    /**
     * Saves all files belonging to an event to storage
     * @return the event after it was saved. **This will be different from the given [encryptedEvent]!**
     * This is because the path of the file will be changed to a path in the device's local storage.
     */
    fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent>

    fun get(uuid: UUID): Single<EncryptedEventEntity>
}