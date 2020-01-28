package be.hogent.faith.database.storage

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Single

// TODO: maybe split in multiple interfaces depending on what is needed?
// e.g. an encryptedStorageRepository and an "isXAvailableChecker"
interface ILocalFileStorageRepository {

    /**
     * Saves all files belonging to an event to local storage
     * @return the event after it was saved. **This will be different from the given [encryptedEvent]!**
     * This is because the path of the file will be changed to a path in the device's local storage.
     */
    fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent>

    /**
     * checks if file is present in localStorage
     */
    fun isFilePresent(detail: Detail): Boolean

    /**
     * checks if emotion avatar is present in localStorage
     */
    fun isEmotionAvatarPresent(event: Event): Boolean
}