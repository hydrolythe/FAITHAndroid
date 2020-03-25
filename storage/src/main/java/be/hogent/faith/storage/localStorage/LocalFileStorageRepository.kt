package be.hogent.faith.storage.localStorage

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.storage.ILocalFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.File

class LocalFileStorageRepository(
    private val pathProvider: StoragePathProvider
) : ILocalFileStorageRepository {

    override fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        return Completable.mergeArray(
            saveEmotionAvatar(encryptedEvent),
            saveEventDetails(encryptedEvent)
        )
            .andThen(
                Single.just(encryptedEvent)
            )
            .doOnSuccess {
                Timber.d("Saved encrypted event to local storage")
            }
    }

    /**
     * Moves the emotion avatar of an event from temporary storage to local storage
     * and updates the path
     */
    private fun saveEmotionAvatar(encryptedEvent: EncryptedEvent): Completable {
        return if (encryptedEvent.emotionAvatar == null) {
            Completable.complete()
        } else {
            Completable.fromCallable {
                moveAvatarToLocalStorage(encryptedEvent)
            }
        }
    }

    /**
     * Moves all detail files of an event from temporary storage to local storage
     * and updates the path.
     */
    private fun saveEventDetails(encryptedEvent: EncryptedEvent): Completable {
        return Observable.fromIterable(encryptedEvent.details)
            .flatMapCompletable { detail ->
                Completable.fromCallable {
                    moveDetailToLocalStorage(detail, encryptedEvent)
                }
            }
    }

    private fun moveDetailToLocalStorage(
        encryptedDetail: EncryptedDetail,
        encryptedEvent: EncryptedEvent
    ) {
        val relativePath = pathProvider.getDetailPath(encryptedEvent, encryptedDetail)
        val finalPath = pathProvider.localStoragePath(relativePath)
        moveFile(encryptedDetail.file, finalPath)
        encryptedDetail.file = finalPath
    }

    private fun moveAvatarToLocalStorage(encryptedEvent: EncryptedEvent) {
        val relativePath = pathProvider.getEmotionAvatarPath(encryptedEvent)
        val finalPath = pathProvider.localStoragePath(relativePath)
        moveFile(encryptedEvent.emotionAvatar!!, finalPath)
        encryptedEvent.emotionAvatar = finalPath
    }

    /**
     * moves a file from tempory storage to local storage and deletes the file in tempory storage
     */
    private fun moveFile(from: File, to: File) {
        from.copyTo(target = to, overwrite = true)
        from.delete()
    }

    override fun isFilePresent(detail: Detail, event: Event): Boolean {
        return with(pathProvider) { localStoragePath(getDetailPath(detail, event)).exists() }
    }

    override fun isEmotionAvatarPresent(event: Event): Boolean {
        if (event.emotionAvatar == null) {
            return true
        } else {
            return with(pathProvider) { localStoragePath(getEmotionAvatarPath(event)).exists() }
        }
    }
}
