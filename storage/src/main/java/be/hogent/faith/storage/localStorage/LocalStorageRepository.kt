package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import be.hogent.faith.storage.encryption.IFileEncrypter
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class LocalStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val fileEncrypter: IFileEncrypter,
    private val context: Context
) : ILocalStorageRepository {

    override fun saveEvent(event: Event): Single<Event> {
        return saveEmotionAvatar(event)
            .mergeWith(saveEventDetails(event))
            .toSingle { event }
    }

    private fun saveEventDetails(event: Event): Completable {
        return Completable.fromCallable {
            event.details.map { detail ->
                detail.file.copyTo(pathProvider.getLocalDetailPath(event, detail))
                fileEncrypter.encrypt(detail.file)
                detail.file.delete()
                detail.file = pathProvider.getDetailPath(event, detail)
            }
        }
    }

    private fun saveEmotionAvatar(event: Event): Completable {
        return Completable.fromCallable {
            if (event.emotionAvatar != null) {
                event.emotionAvatar!!.copyTo(pathProvider.getLocalEmotionAvatarPath(event))
                fileEncrypter.encrypt(event.emotionAvatar!!)
                event.emotionAvatar!!.delete()
                event.emotionAvatar = pathProvider.getEmotionAvatarPath(event)
            }
        }
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
