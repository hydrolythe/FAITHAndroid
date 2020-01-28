package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.storage.ILocalFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.encryption.IFileEncrypter
import io.reactivex.Single
import java.io.File

class LocalFileStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val fileEncrypter: IFileEncrypter,
    private val context: Context
) : ILocalFileStorageRepository {

    override fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        // TODO: make more reactive by making subcalls reactive instead of procedural
        return Single.fromCallable {
            saveEmotionAvatar(encryptedEvent)
            saveEventDetails(encryptedEvent)
            encryptedEvent
        }
    }

    /**
     * moves all detail files of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEventDetails(encryptedEvent: EncryptedEvent) {
        encryptedEvent.details.map { encryptedDetail ->
            moveFile(
                encryptedDetail.file,
                pathProvider.getLocalDetailPath(encryptedEvent, encryptedDetail)
            )
            encryptedDetail.file = pathProvider.getDetailPath(encryptedEvent, encryptedDetail)
            fileEncrypter.encrypt(encryptedDetail.file)
        }
    }

    /**
     * moves the emotion avatar of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEmotionAvatar(encryptedEvent: EncryptedEvent) {
        if (encryptedEvent.emotionAvatar != null) {
            moveFile(
                encryptedEvent.emotionAvatar!!,
                pathProvider.getLocalEmotionAvatarPath(encryptedEvent)
            )
            encryptedEvent.emotionAvatar = pathProvider.getEmotionAvatarPath(encryptedEvent)
            fileEncrypter.encrypt(encryptedEvent.emotionAvatar!!)
        }
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
