package be.hogent.faith.storage.storage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.firebase.IFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class RemoteStorage(private val firebaseStorage: IFirebaseStorage) : IStorage {
    override fun storeBitmap(bitmap: Bitmap, file: File): Completable {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun deleteFile(file: File): Boolean {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun saveEventAudio(tempStorageFile: File, event: Event): Single<File> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun saveEventDrawing(bitmap: Bitmap, event: Event): Single<File> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun saveText(text: String, event: Event): Single<File> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun overwriteEventDetail(bitmap: Bitmap, existingDetail: DrawingDetail): Completable {
        throw UnsupportedOperationException("operation isn't supported here...")
    }

    override fun saveEventEmotionAvatar(event: Event): Single<File> {
        return firebaseStorage.saveEventEmotionAvatar(event)
    }

    override fun saveDetailFile(event: Event, detail: Detail): Single<File> {
        return firebaseStorage.saveDetailFile(event, detail)
    }

    override fun moveFilesFromRemoteStorageToCacheStorage(event: Event): Completable {
        return firebaseStorage.moveFilesFromRemoteStorageToLocalStorage(event)
    }
}