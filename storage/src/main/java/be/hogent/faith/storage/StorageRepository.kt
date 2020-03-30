package be.hogent.faith.storage

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.IFireBaseStorageRepository
import be.hogent.faith.storage.localStorage.ILocalStorageRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import java.io.File

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class StorageRepository(
    private val localStorage: ILocalStorageRepository,
    private val remoteStorage: IFireBaseStorageRepository,
    private val pathProvider: StoragePathProvider
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
    private fun getFileLocally(detail: Detail): Completable {
        if (localStorage.isFilePresent(detail))
            return Completable.complete()
        else
            return remoteStorage.makeFileLocallyAvailable(detail)
    }

    override fun getFile(detail: Detail): Single<File> {
        return getFileLocally(detail).toSingle { pathProvider.getLocalDetailPath(detail) }
    }

    override fun saveDetailFileForContainer(
        detailsContainer: DetailsContainer,
        detail: Detail
    ): Single<Detail> {
        return localStorage.saveDetailFileForContainer(detailsContainer, detail)
            .flatMap { remoteStorage.saveDetailFileForContainer(detailsContainer, it) }
    }

    override fun deleteDetail(detail: Detail, event: Event): Completable {
        // TODO: aanvullen met encryptie
        return Completable.complete()
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
                        getFileLocally(it)
                    }
            )
    }
}