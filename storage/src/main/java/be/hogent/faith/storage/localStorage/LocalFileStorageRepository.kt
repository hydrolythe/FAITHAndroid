package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.storage.ILocalFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

class LocalFileStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val context: Context
) : ILocalFileStorageRepository {

    override fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        return Completable.mergeArray(
            saveEmotionAvatar(encryptedEvent),
            saveEventDetails(encryptedEvent)
        ).andThen(
            Single.just(encryptedEvent)
        )
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
        it: EncryptedDetail,
        encryptedEvent: EncryptedEvent
    ) {
        moveFile(
            it.file,
            pathProvider.getLocalDetailPath(encryptedEvent, it)
        )
        it.file = pathProvider.getDetailPath(encryptedEvent, it)
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

    private fun moveAvatarToLocalStorage(encryptedEvent: EncryptedEvent) {
        moveFile(
            encryptedEvent.emotionAvatar!!,
            pathProvider.getLocalEmotionAvatarPath(encryptedEvent)
        )
        encryptedEvent.emotionAvatar = pathProvider.getEmotionAvatarPath(encryptedEvent)
    }

    /**
     * moves a file from tempory storage to local storage and deletes the file in tempory storage
     */
    private fun moveFile(from: File, to: File) {
        from.copyTo(target = to, overwrite = true)
        from.delete()
    }

    override fun isFilePresent(detail: Detail): Boolean {
        return File(context.filesDir, detail.file.path).exists()
    }

    override fun isEmotionAvatarPresent(event: Event): Boolean {
        if (event.emotionAvatar == null)
            return true
        return File(context.filesDir, event.emotionAvatar!!.path).exists()
    }
}
