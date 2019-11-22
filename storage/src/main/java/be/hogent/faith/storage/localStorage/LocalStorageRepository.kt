package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class LocalStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val context: Context
) : IStorageRepository {
    override fun saveEvent(event: Event): Completable {
        val saveAvatar = saveEmotionAvatar(event)
        val saveDetails = saveEventDetails(event)
        return Completable.mergeArray(saveAvatar, saveDetails)
    }

    private fun saveEventDetails(event: Event): Completable {
        return Completable.merge(
            event.details.map { detail ->
                moveFile(detail.file, pathProvider.getLocalDetailPath(detail, event))
            }
        )
    }

    private fun saveEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar != null) {
            return moveFile(event.emotionAvatar!!, pathProvider.getLocalEmotionAvatarPath(event))
        } else {
            return Completable.complete()
        }
    }

    private fun moveFile(from: File, to: File): Completable {
        return Completable.fromCallable {
            from.copyTo(target = to, overwrite = true)
            from.delete()
        }
    }


    override fun getFile(detail: Detail): Single<File> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteFile(file: File): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getEventDirectory(event: Event): File {
        val eventDir = File(context.filesDir, pathProvider.getEventFolder(event).path)
        eventDir.mkdirs()
        return eventDir
    }
}
