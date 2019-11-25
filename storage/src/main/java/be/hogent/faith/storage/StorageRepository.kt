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

    // TODO: nakijken: niet alles zit direct in een file. Bij het laden van een textDetail werd
    //  er tot nu toe bvb de string teruggegeven die in de file zat. Voor audio en beeld is een file wel nog ok.
    //  Maken we hier dan een uitbreiding voor?
    override fun getFile(detail: Detail): Single<File> {
        return localStorage.getFile(detail)
            .onErrorResumeNext { remoteStorage.getFile(detail) }
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        return localStorage.getEmotionAvatar(event)
            .onErrorResumeNext { remoteStorage.getEmotionAvatar(event) }
    }
}