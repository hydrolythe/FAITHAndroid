package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.database.encryption.EncryptedEventEntity
import be.hogent.faith.database.storage.ILocalStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.encryption.IFileEncrypter
import io.reactivex.Completable
import java.io.File

class LocalStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val fileEncrypter: IFileEncrypter,
    private val context: Context
) : ILocalStorageRepository {

    override fun saveEvent(encryptedEventEntity: EncryptedEventEntity): Completable {
        //TODO: make more reactive by making subcalls reactive instead of procdural
        return Completable.fromCallable {
            saveEmotionAvatar(encryptedEventEntity)
            saveEventDetails(encryptedEventEntity)
            encryptedEventEntity
        }
    }

    /**
     * moves all detail files of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEventDetails(event: EncryptedEventEntity) {
        event.detailEntities.map { detail ->
            // TODO: encryptedDetailEntity can retain a file type instead of a string as it's not encrypted
            moveFile(File(detail.file), pathProvider.getLocalDetailPath(event, detail))
            detail.file = pathProvider.getDetailPath(event, detail)
            fileEncrypter.encrypt(detail.file)
        }
    }

    /**
     * moves the emotion avatar of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEmotionAvatar(event: EncryptedEventEntity) {
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

    override fun isFilePresent(detail: Detail): Boolean {
        return File(context.filesDir, detail.file.path).exists()
    }

    override fun isEmotionAvatarPresent(event: Event): Boolean {
        if (event.emotionAvatar == null)
            return true
        return File(context.filesDir, event.emotionAvatar!!.path).exists()
    }
}
