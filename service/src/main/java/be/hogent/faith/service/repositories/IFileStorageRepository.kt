package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import io.reactivex.Completable
import io.reactivex.Single

interface IFileStorageRepository {

    /**
     * Saves all files belonging to an event to storage
     * @return the event after it was saved. **This will be different from the given [encryptedEvent]!**
     * This is because the path of the file will be changed to a path in the device's local storage.
     */
    fun saveEventFiles(encryptedEvent: EncryptedEvent): Single<EncryptedEvent>

    /**
     * Downloads the [event]s files. If they are already on the devices storage, this completes instantly.
     */
    fun downloadEventFiles(event: Event): Completable

    /**
     * Files are considered ready to use if they are available on the device in an unencrypted format.
     */
    fun filesReadyToUse(event: Event): Boolean

    /**
     * Downloads a detail's file, and returns it.
     */
    fun downloadFile(detail: Detail, container: DetailsContainer): Completable

    /**
     * Sets the detail's file if it is ready to use.
     * Files are considered ready to use if they are available on the device in an unencrypted format.
     * @return false if the file isn't ready to use yet.
     */
    fun setFileIfReady(detail: Detail, container: DetailsContainer): Boolean

    fun saveDetailFileWithContainer(
        encryptedDetail: EncryptedDetail,
        container: DetailsContainer
    ): Single<EncryptedDetail>

    /**
     * Delete all files belonging to this detail.
     */
    fun deleteFiles(detail: Detail, container: DetailsContainer): Completable
}