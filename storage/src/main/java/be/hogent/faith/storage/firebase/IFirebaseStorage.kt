package be.hogent.faith.storage.firebase

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface IFirebaseStorage {
    fun saveEventEmotionAvatar(event: Event): Single<File>
    fun saveDetailFile(event: Event, detail: Detail): Single<File>
    fun moveFilesFromRemoteStorageToCacheStorage(event: Event): Completable
}