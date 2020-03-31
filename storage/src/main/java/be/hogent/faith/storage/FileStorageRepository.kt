package be.hogent.faith.storage

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.database.storage.ILocalFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.onlinestorage.IOnlineFileStorageRepository
import be.hogent.faith.storage.localstorage.ITemporaryStorageRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class FileStorageRepository(
    private val temporaryStorageRepository: ITemporaryStorageRepository,
    private val localFileStorage: ILocalFileStorageRepository,
    private val remoteFileStorage: IOnlineFileStorageRepository
) : IFileStorageRepository {

    /**
     * stores all event files in permanent storage, both local and online.
     * @return the saved event. This can be different from the original
     * @see ILocalFileStorageRepository.saveEvent
     */
    override fun saveEventFiles(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        // Hacky way to first store locally, then remotely, and then give back the event.
        // Must be done in this order because saving to local storage changes paths inside the event.
        return localFileStorage.saveEvent(encryptedEvent)
            .ignoreElement()
            .andThen(remoteFileStorage.saveEvent(encryptedEvent))
            .toSingle { encryptedEvent }
    }

    /**
     * Transfers a [detail]'s file(s) from online storage to local storage.
     * If the files were already in local storage, completes immediately.
     *
     * @param event: the event this detail belongs to. Used to determine the path where the
     * file should be stored.
     */
    private fun getDetailFile(detail: Detail, event: Event): Completable {
        if (localFileStorage.isFilePresent(detail, event))
            return Completable.complete()
        else
            return remoteFileStorage.downloadDetail(detail, event)
    }

    /**
     * download emotion avatar from firebase to localStorage if not present yet
     */
    private fun getEmotionAvatarFile(event: Event): Completable {
        if (event.emotionAvatar == null || localFileStorage.isEmotionAvatarPresent(event))
            return Completable.complete()
        else
            return remoteFileStorage.downloadEmotionAvatar(event)
    }

    /**
     * Download all files belonging to an event
     */
    override fun downloadEventFiles(event: Event): Completable {
        return Completable.mergeArray(
            getEmotionAvatarFile(event),
            Observable.fromIterable(event.details)
                .flatMapCompletable { getDetailFile(it, event) }
        )
    }

    override fun filesReadyToUse(event: Event): Boolean {
        return temporaryStorageRepository.isEmotionAvatarPresent(event) &&
                event.details.all { detail ->
                    // We  are using the fact that decrypted files are stored in the cache directory.
                    temporaryStorageRepository.isFilePresent(detail, event)
                }
    }
}