package be.hogent.faith.storage

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.storage.ILocalFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.IOnlineFileStorageRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class FileStorageRepository(
    private val localFileStorage: ILocalFileStorageRepository,
    private val remoteFileStorage: IOnlineFileStorageRepository
) : IFileStorageRepository {

    /**
     * stores all event files in permanent storage, both local and online.
     * @return the saved event. This can be different from the original
     * @see ILocalFileStorageRepository.saveEvent
     */
    override fun saveEvent(encryptedEvent: EncryptedEvent): Single<EncryptedEvent> {
        // Hacky way to first store locally, then remotely, and then give back the event.
        // Must be done in this order because saving to localstorage changes paths inside the event.
        return localFileStorage.saveEvent(encryptedEvent)
            .ignoreElement()
            .andThen(remoteFileStorage.saveEvent(encryptedEvent))
            .toSingle { encryptedEvent }
    }

    /**
     * download file from firebase to localStorage if not present yet
     */
    // TODO ("Timestamp checking? What als de file op een andere tablet werd aangepast?")
    private fun getFile(detail: Detail): Completable {
        if (localFileStorage.isFilePresent(detail))
            return Completable.complete()
        else
            return remoteFileStorage.downloadDetail(detail)
    }

    /**
     * download emotion avatar from firebase to localStorage if not present yet
     */
    private fun getEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null || localFileStorage.isEmotionAvatarPresent(event))
            return Completable.complete()
        else
            return remoteFileStorage.downloadEmotionAvatar(event)
    }

    /**
     * download all event files from firebase to localStorage if not present yet
     */
    override fun downloadEventFiles(event: Event): Completable {
        return getEmotionAvatar(event)
            .concatWith(
                event.details.toFlowable()
                    .concatMapCompletable {
                        getFile(it)
                    }
            )
    }
}