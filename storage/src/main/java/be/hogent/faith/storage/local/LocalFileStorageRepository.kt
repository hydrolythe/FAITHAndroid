package be.hogent.faith.storage.local

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

class LocalFileStorageRepository(
    private val pathProvider: StoragePathProvider
) : ILocalFileStorageRepository {

    override fun saveEventFiles(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        return Completable
            .mergeArray(
                saveEmotionAvatar(encryptedEvent),
                saveEventDetails(encryptedEvent)
            )
            .andThen(
                Single.just(encryptedEvent)
            )
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
        // There is no valid File to move for a YoutubeVideoDetail
        if (encryptedDetail.youtubeVideoID.isNotEmpty()) {
            return
        }
        val localStoragePath =
            with(pathProvider) { localStorage(detailPath(encryptedEvent, encryptedDetail)) }
        moveFile(encryptedDetail.file, localStoragePath)
        encryptedDetail.file = localStoragePath
    }

    private fun moveAvatarToLocalStorage(encryptedEvent: EncryptedEvent) {
        val localStoragePath =
            with(pathProvider) { localStorage(emotionAvatarPath(encryptedEvent)) }
        moveFile(encryptedEvent.emotionAvatar!!, localStoragePath)
        encryptedEvent.emotionAvatar = localStoragePath
    }

    /**
     * moves a file from tempory storage to local storage and deletes the file in tempory storage
     */
    private fun moveFile(from: File, to: File) {
        from.copyTo(target = to, overwrite = true)
        from.delete()
    }

    override fun isFilePresent(detail: Detail, event: Event): Boolean {
        // As there's no file in a YoutubeVideoDetail, we say yes
        if (detail is YoutubeVideoDetail) {
            return true
        } else {
            return with(pathProvider) { localStorage(detailPath(detail, event)).exists() }
        }
    }

    override fun isEmotionAvatarPresent(event: Event): Boolean {
        if (event.emotionAvatar == null) {
            return true
        } else {
            // TODO: check if this enough: the event's avatar should also point to this path
            return with(pathProvider) { localStorage(emotionAvatarPath(event)).exists() }
        }
    }

    override fun saveDetailFileWithContainer(
        detail: EncryptedDetail,
        container: DetailsContainer
    ): Single<EncryptedDetail> {
        // As there's no file in a YoutubeVideoDetail, we can just return it
        if (detail.youtubeVideoID.isNotEmpty()) {
            return Single.just(detail)
        } else {
            return Single.fromCallable {
                val localPath = with(pathProvider) { localStorage(detailPath(detail, container)) }
                moveFile(detail.file, localPath)
                detail.file = localPath
                detail
            }
        }
    }
}
