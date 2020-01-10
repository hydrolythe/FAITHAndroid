package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.encryption.IFileEncrypter
import io.reactivex.Single
import java.io.File

class LocalStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val fileEncrypter: IFileEncrypter,
    private val context: Context
) : ILocalStorageRepository {

    /**
     * moves the files from tempory storage to local storage
     */
    override fun saveEvent(event: Event): Single<Event> {
        return Single.fromCallable {
            saveEmotionAvatar(event)
            saveEventDetails(event)
            event
        }
    }

    /**
     * moves all detail files of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEventDetails(event: Event) {
        event.details.map { detail ->
            moveFile(detail.file, pathProvider.getLocalDetailPath(event, detail))
            detail.file = pathProvider.getDetailPath(event, detail)
            fileEncrypter.encrypt(detail.file)
        }
    }

    /**
     * moves the emotion avatar of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEmotionAvatar(event: Event) {
        if (event.emotionAvatar != null) {
            moveFile(
                event.emotionAvatar!!,
                pathProvider.getLocalEmotionAvatarPath(event)
            )
            event.emotionAvatar = pathProvider.getEmotionAvatarPath(event)
            fileEncrypter.encrypt(event.emotionAvatar!!)
        }
    }

    /**
     * moves a file from tempory storage to local storage and deletes the file in tempory storage
     */
    private fun moveFile(from: File, to: File) {
        from.copyTo(target = to, overwrite = true)
        from.delete()
    }

    /**
     * checks if file is present in localStorage
     */
    override fun isFilePresent(detail: Detail): Boolean {
        return File(context.filesDir, detail.file.path).exists()
    }

    /**
     * checks if emotion avatar is present in localStorage
     */
    override fun isEmotionAvatarPresent(event: Event): Boolean {
        if (event.emotionAvatar == null)
            return true
        return File(context.filesDir, event.emotionAvatar!!.path).exists()
    }
}
