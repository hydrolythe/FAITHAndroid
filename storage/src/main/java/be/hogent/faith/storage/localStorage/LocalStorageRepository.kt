package be.hogent.faith.storage.localStorage

import android.content.Context
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Single
import java.io.File

class LocalStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val context: Context
) : ILocalStorageRepository {

    /**
     * moves a detailfile from tempory storage to local storage, deletes the file in tempory storage, update details path and returns the detail
     */
    override fun saveDetailFileForContainer(detailsContainer: DetailsContainer, detail:Detail): Single<Detail> {
        return Single.fromCallable {
            moveDetail(detailsContainer, detail)
            detail
        }
    }

    /**
     * moves the files (avatar and details) from tempory storage to local storage for an event
     */
    override fun saveEvent(event: Event): Single<Event> {
        return Single.fromCallable {
            saveEmotionAvatar(event)
            saveEventDetails(event)
            event
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
        }
    }

    /**
     * moves all detail files of an event from tempory storage to local storage
     * and updates the path
     */
    private fun saveEventDetails(event: Event) {
        event.details.map { detail -> moveDetail(event, detail) }
    }

    /**
     * moves a file from tempory storage to local storage and deletes the file in tempory storage
     */
    private fun moveFile(from: File, to: File) {
        from.copyTo(target = to, overwrite = true)
        from.delete()
    }

    /**
     * moves a detailfile from tempory storage to local storage and deletes the file in tempory storage and updates the detail
     */
    private fun moveDetail(detailsContainer:DetailsContainer, detail:Detail) {
        moveFile(detail.file, pathProvider.getLocalDetailPath(detailsContainer, detail))
        detail.file = pathProvider.getDetailPath(detailsContainer, detail)
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
