package be.hogent.faith.storage

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.FireBaseStorage
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
    private val remoteStorage: FireBaseStorage
) : IStorage {


    override fun saveEvent(event: Event): Completable {
       return localStorage.saveEvent(event).andThen(remoteStorage.saveEvent(event))
    }

    override fun getFile(detail: Detail): Single<File> {
        return localStorage.getFile(detail)
            .onErrorResumeNext{remoteStorage.getFile(detail) }
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        return localStorage.getEmotionAvatar(event).onErrorResumeNext{remoteStorage.getEmotionAvatar(event) }
    }
}