package be.hogent.faith.storage.storage

import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.util.UUID

interface IStorage {
    //relatief pad
    fun getUser(): User{
        return User("hkhlkl", "j", UUID.randomUUID().toString())
    }

    fun getEventFolder(event: Event): File {
        return File("users/${getUser().uuid}/events/${event.uuid}")
    }

    fun getDetailPath(event: Event, detail: Detail): File {
        return File("${getEventFolder(event).path}/${detail.uuid}")
    }

    fun getEmotionAvatarPath(event: Event): File {
        return File("${getEventFolder(event).path}/avatar")
    }

    fun storeBitmap(bitmap: Bitmap, file: File): Completable
    fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File>
    fun deleteFile(file: File): Boolean
    fun saveEventAudio(tempStorageFile: File, event: Event): Single<File>
    fun saveEventDrawing(bitmap: Bitmap, event: Event): Single<File>
    fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File>
    fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File>
    fun saveText(text: String, event: Event): Single<File>
    fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String>
    fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable
    fun overwriteEventDetail(bitmap: Bitmap, existingDetail: DrawingDetail): Completable
    fun saveEventEmotionAvatar(event: Event): Single<File>
    fun saveDetailFile(event: Event, detail: Detail): Single<File>
    fun moveFileFromRemoteStorageToLocalStorage(event: Event): Completable
}