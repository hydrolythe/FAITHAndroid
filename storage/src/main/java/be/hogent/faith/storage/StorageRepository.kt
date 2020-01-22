package be.hogent.faith.storage

import be.hogent.faith.database.encryption.EncryptedEventEntity
import be.hogent.faith.database.storage.ILocalStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.firebase.IOnlineStorageRepository
import io.reactivex.Completable
import io.reactivex.rxkotlin.toFlowable

/**
 * Repository providing access to both the internal and remote storage.
 * It decides which one will be used based on the network availability.
 *
 */
class StorageRepository(
    private val localStorage: ILocalStorageRepository,
    private val remoteStorage: IOnlineStorageRepository
) : IStorageRepository {

    /**
     * stores all event files in firebase
     */
    override fun saveEvent(encryptedEventEntity: EncryptedEventEntity): Completable {
        // TODO: do in parallel?
        return localStorage.saveEvent(encryptedEventEntity).andThen(
            remoteStorage.saveEvent(encryptedEventEntity)
        )
    }

    /**
     * download file from firebase to localStorage if not present yet
     */
    // TODO ("Timestamp checking? What als de file op een andere tablet werd aangepast?")
    private fun getFile(detail: Detail): Completable {
        if (localStorage.isFilePresent(detail))
            return Completable.complete()
        else
            return remoteStorage.downloadDetail(detail)
    }

    /**
     * download emotion avatar from firebase to localStorage if not present yet
     */
    private fun getEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null || localStorage.isEmotionAvatarPresent(event))
            return Completable.complete()
        else
            return remoteStorage.downloadEmotionAvatar(event)
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