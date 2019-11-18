package be.hogent.faith.storage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.storage.StorageFactory
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

const val EMOTION_AVATAR_FILENAME = "emotionAvatar"

/**
 * Repository providing access to both the internal and external storage.
 * It decides which one will be used based on the user's settings.
 *
 * TODO: Currently only supports internal storage.
 */
const val TEXT_EXTENSION = "txt"

class StorageRepository(private val storageFactory: StorageFactory) {

    fun storeBitmap(bitmap: Bitmap, file: File): Completable {
        return storageFactory.getLocalStorage().storeBitmap(bitmap, file)
    }

    /**
     * Stores the bitmap on the device's storage.
     * It will be put in the context.filesDir/event.uuid/images/ folder
     *
     * @param fileName under which the bitmap will be saved
     * @return a Single<File> with the path derived from the event's dateTime
     */
    fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File> {
        return storageFactory.getLocalStorage().storeBitmap(bitmap, folder, fileName)
    }

    fun deleteFile(file: File): Boolean {
        return storageFactory.getLocalStorage().deleteFile(file)
    }

    fun saveEventAudio(tempStorageFile: File, event: Event): Single<File> {
        return storageFactory.getLocalStorage().saveEventAudio(tempStorageFile, event)
    }

    fun saveEventDrawing(bitmap: Bitmap, event: Event): Single<File> {
        return storageFactory.getLocalStorage().saveEventDrawing(bitmap, event)
    }

    fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File> {
        return storageFactory.getLocalStorage().saveEventPhoto(tempStorageFile, event)
    }

    fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File> {
        return storageFactory.getLocalStorage().saveEventEmotionAvatar(bitmap, event)
    }

    fun saveText(text: String, event: Event): Single<File> {
        return storageFactory.getLocalStorage().saveText(text, event)
    }

    fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return storageFactory.getLocalStorage().loadTextFromExistingDetail(textDetail)
    }

    fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return storageFactory.getLocalStorage().overwriteTextDetail(text, existingDetail)
    }

    fun overwriteEventDetail(bitmap: Bitmap, existingDetail: DrawingDetail): Completable {
        return storageFactory.getLocalStorage().overwriteEventDetail(bitmap, existingDetail)
    }

    fun saveEventEmotionAvatar(event: Event): Single<File> {
        return storageFactory.getRemoteDataStore().saveEventEmotionAvatar(event)
    }

    fun saveDetailFile(event: Event, detail: Detail): Single<File> {
        return storageFactory.getRemoteDataStore().saveDetailFile(event, detail)
    }

    fun moveFilesFromRemoteStorageToLocalStorage(event: Event): Completable {
        return storageFactory.getRemoteDataStore().moveFileFromRemoteStorageToLocalStorage(event)
    }
}