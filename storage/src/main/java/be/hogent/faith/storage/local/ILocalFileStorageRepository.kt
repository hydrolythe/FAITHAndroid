package be.hogent.faith.storage.local

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedEvent
import io.reactivex.Completable
import io.reactivex.Single

interface ILocalFileStorageRepository {

    /**
     * Saves all files belonging to an event to local storage
     * @return the event after it was saved. **This will be different from the given [encryptedEvent]!**
     * This is because the path of the file will be changed to a path in the device's local storage.
     */
    fun saveEventFiles(encryptedEvent: EncryptedEvent): Single<EncryptedEvent>

    /**
     * Checks if file is present in localStorage
     */
    fun isFilePresent(detail: Detail, event: Event): Boolean

    /**
     * Checks if emotion avatar is present in localStorage
     */
    fun isEmotionAvatarPresent(event: Event): Boolean

    fun saveDetailFileWithContainer(detail: Detail, container: DetailsContainer): Completable
}