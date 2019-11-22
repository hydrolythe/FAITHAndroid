package be.hogent.faith.storage

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.FireBaseStorageRepository
import be.hogent.faith.storage.localStorage.LocalStorageRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class StorageRepository(
    private val localStorage: LocalStorageRepository,
    private val remoteStorage: FireBaseStorageRepository
) : IStorageRepository {

    override fun saveEvent(event: Event): Completable {
        return localStorage.saveEvent(event).andThen(remoteStorage.saveEvent(event))
    }

    // Used by eg Glide to get a file reference for a detail
    override fun getFile(detail: Detail): Single<File> {
        // Eerst kijken of het er lokaal is, indien niet downloaden en de remotestorage slaat die lokaal op
        return localStorage.getFile(detail)
            .onErrorResumeNext { remoteStorage.getFile(detail) }
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        // Zelfde als bij getFile
        return localStorage.getEmotionAvatar(event)
            .onErrorResumeNext { remoteStorage.getEmotionAvatar(event) }
    }

//    override fun deleteFile(file: File): Completable {
//        return localStorage.deleteFile(file).andThen(remoteStorage.deleteFile(file))
//    }
}