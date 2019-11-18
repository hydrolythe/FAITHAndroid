package be.hogent.faith.storage.storage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.localStorage.ILocalStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class LocalStorage(private val localStorageImpl: ILocalStorage) : IStorage {
    override fun saveEventEmotionAvatar(event: Event): Single<File> {
        throw UnsupportedOperationException("saving emotionAvatar isn't supported here...")
    }

    override fun saveDetailFile(event: Event, detail: Detail): Single<File> {
        throw UnsupportedOperationException("saving detailFile isn't supported here...")
    }

    override fun moveFileFromRemoteStorageToLocalStorage(event: Event): Completable {
        throw UnsupportedOperationException("downloading files isn't supported here...")
    }

    override fun deleteFile(file: File): Boolean {
        return localStorageImpl.deleteFile(file)
    }

    override fun saveEventAudio(tempStorageFile: File, event: Event): Single<File> {
        return localStorageImpl.saveEventAudio(tempStorageFile, event)
    }

    override fun saveEventDrawing(bitmap: Bitmap, event: Event): Single<File> {
        return localStorageImpl.saveEventDrawing(bitmap, event)
    }

    override fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File> {
        return localStorageImpl.saveEventPhoto(tempStorageFile, event)
    }

    override fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File> {
        return localStorageImpl.saveEventEmotionAvatar(bitmap, event)
    }

    override fun saveText(text: String, event: Event): Single<File> {
        return localStorageImpl.saveText(text, event)
    }

    override fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return localStorageImpl.loadTextFromExistingDetail(textDetail)
    }

    override fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return localStorageImpl.overwriteTextDetail(text, existingDetail)
    }

    override fun overwriteEventDetail(bitmap: Bitmap, existingDetail: DrawingDetail): Completable {
        return localStorageImpl.overwriteEventDetail(bitmap, existingDetail)
    }

    override fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File> {
        return localStorageImpl.storeBitmap(bitmap, folder, fileName)
    }

    override fun storeBitmap(bitmap: Bitmap, file: File): Completable {
        return localStorageImpl.storeBitmap(bitmap, file)
    }
}