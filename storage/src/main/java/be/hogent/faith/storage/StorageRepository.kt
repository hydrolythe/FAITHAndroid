package be.hogent.faith.storage

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.IFireBaseStorageRepository
import be.hogent.faith.storage.localStorage.ILocalStorageRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class StorageRepository(
    private val localStorage: ILocalStorageRepository,
    private val remoteStorage: IFireBaseStorageRepository
) : IStorageRepository {

    /**
     * stores all event files in firebase
     */
    override fun saveEvent(event: Event): Single<Event> {
        return localStorage.saveEvent(event)
            .flatMap { remoteStorage.saveEvent(it) }
    }

    /**
     * download file from firebase to localStorage if not present yet
     */
    // TODO ("Timestamp checking? What als de file op een andere tablet werd aangepast?")
    private fun getFile(detail: Detail): Completable {
        if (localStorage.isFilePresent(detail))
            return Completable.complete()
        else
            return remoteStorage.makeFileLocallyAvailable(detail)
    }

    /**
     * download emotion avatar from firebase to localStorage if not present yet
     */
    private fun getEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null || localStorage.isEmotionAvatarPresent(event))
            return Completable.complete()
        else
            return remoteStorage.makeEmotionAvatarLocallyAvailable(event)
    }

    /**
     * download all event files from firebase to localStorage if not present yet
     */
    override fun getEvent(event: Event): Completable {
        return getEmotionAvatar(event)
            .concatWith(
                event.details.toFlowable()
                    .concatMapCompletable {
                        getFile(it)
                    }
            )
    }



}