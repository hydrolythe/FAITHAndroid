package be.hogent.faith.storage

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.storage.local.ILocalFileStorageRepository
import be.hogent.faith.storage.local.ITemporaryFileStorageRepository
import be.hogent.faith.storage.online.IOnlineFileStorageRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class FileStorageRepository(
    private val temporaryStorage: ITemporaryFileStorageRepository,
    private val localStorage: ILocalFileStorageRepository,
    private val onlineStorage: IOnlineFileStorageRepository
) : IFileStorageRepository {

    /**
     * stores all event files in permanent storage, both local and online.
     * @return the saved event. This can be different from the original
     * @see ILocalFileStorageRepository.saveEventFiles
     */
    override fun saveEventFiles(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        // Hacky way to first store locally, then remotely, and then give back the event.
        // Must be done in this order because saving to local storage changes paths inside the event.
        return localStorage.saveEventFiles(encryptedEvent)
            .flatMapCompletable(onlineStorage::saveEventFiles)
            // the original encrypted is also the one that has been changed in localStorage call (pass by reference)
            .andThen(Single.just(encryptedEvent))
    }

    /**
     * Download all files belonging to an event
     */
    override fun downloadEventFiles(event: Event): Completable {
        return Completable.mergeArray(
            getEmotionAvatarFile(event),
            Observable
                .fromIterable(event.details)
                .flatMapCompletable { getDetailFile(it, event) }
        )
    }

    /**
     * Transfers a [detail]'s file(s) from online storage to local storage.
     * If the files were already in local storage, completes immediately.
     *
     * @param event: the event this detail belongs to.
     * Used to determine the path where the file should be stored.
     */
    private fun getDetailFile(detail: Detail, event: Event): Completable {
        if (localStorage.isFilePresent(detail, event))
            return Completable.complete()
        else
            return onlineStorage.downloadDetail(detail, event)
    }

    /**
     * download emotion avatar from firebase to localStorage if not present yet
     */
    private fun getEmotionAvatarFile(event: Event): Completable {
        if (event.emotionAvatar == null || localStorage.isEmotionAvatarPresent(event)) {
            return Completable.complete()
        } else {
            return onlineStorage.downloadEmotionAvatar(event)
        }
    }

    override fun filesReadyToUse(event: Event): Boolean {
        // We  are using the fact that decrypted files are stored in the cache directory.
        return temporaryStorage.isEmotionAvatarPresent(event) &&
                event.details.all { detail ->
                    temporaryStorage.isFilePresent(detail, event)
                }
    }

    override fun downloadFile(detail: Detail, container: DetailsContainer): Completable {
        if (localStorage.isFilePresent(detail, container)) {
            return Completable.complete()
        } else {
            return onlineStorage.downloadDetail(detail, container)
        }
    }

    override fun setFileIfReady(detail: Detail, container: DetailsContainer): Boolean {
        if (temporaryStorage.isFilePresent(detail, container)) {
            detail.file = temporaryStorage.getFile(detail, container)
            return true
        } else {
            return false
        }
    }

    override fun saveDetailFileWithContainer(
        encryptedDetail: EncryptedDetail,
        container: DetailsContainer
    ): Single<EncryptedDetail> {
        return localStorage.saveDetailFileWithContainer(encryptedDetail, container)
            .flatMapCompletable { savedDetail ->
                onlineStorage.saveDetailFiles(
                    savedDetail,
                    container
                )
            }
            .andThen(Single.just(encryptedDetail))
    }

    override fun deleteFiles(detail: Detail, container: DetailsContainer): Completable {
        return Completable.mergeArray(
            deleteDetailFile(detail),
            temporaryStorage.deleteFiles(detail, container),
            localStorage.deleteFiles(detail, container),
            onlineStorage.deleteFiles(detail, container)
        )
    }

    private fun deleteDetailFile(detail: Detail): Completable {
        return Completable.fromAction {
            if (detail.file.exists()) {
                detail.file.delete()
            }
        }
    }
}