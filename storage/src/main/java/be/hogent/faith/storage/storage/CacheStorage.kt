package be.hogent.faith.storage.storage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.cache.ICacheStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class CacheStorage(private val cacheStorageImpl: ICacheStorage) : IStorage {
    override fun saveEventEmotionAvatar(event: Event): Single<File> {
        throw UnsupportedOperationException("saving emotionAvatar isn't supported here...")
    }

    override fun saveDetailFile(event: Event, detail: Detail): Single<File> {
        throw UnsupportedOperationException("saving detailFile isn't supported here...")
    }

    override fun moveFilesFromRemoteStorageToCacheStorage(event: Event): Completable {
        throw UnsupportedOperationException("downloading files isn't supported here...")
    }

    override fun deleteFile(file: File): Boolean {
        return cacheStorageImpl.deleteFile(file)
    }

    override fun saveEventAudio(tempStorageFile: File, event: Event): Single<File> {
        return cacheStorageImpl.saveEventAudio(tempStorageFile, event)
    }

    override fun saveEventDrawing(bitmap: Bitmap, event: Event): Single<File> {
        return cacheStorageImpl.saveEventDrawing(bitmap, event)
    }

    override fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File> {
        return cacheStorageImpl.saveEventPhoto(tempStorageFile, event)
    }

    override fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File> {
        return cacheStorageImpl.saveEventEmotionAvatar(bitmap, event)
    }

    override fun saveText(text: String, event: Event): Single<File> {
        return cacheStorageImpl.saveText(text, event)
    }

    override fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return cacheStorageImpl.loadTextFromExistingDetail(textDetail)
    }

    override fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return cacheStorageImpl.overwriteTextDetail(text, existingDetail)
    }

    override fun overwriteEventDetail(bitmap: Bitmap, existingDetail: DrawingDetail): Completable {
        return cacheStorageImpl.overwriteEventDetail(bitmap, existingDetail)
    }

    override fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File> {
        return cacheStorageImpl.storeBitmap(bitmap, folder, fileName)
    }

    override fun storeBitmap(bitmap: Bitmap, file: File): Completable {
        return cacheStorageImpl.storeBitmap(bitmap, file)
    }
}