package be.hogent.faith.storage.firebase

import android.net.Uri
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.rxkotlin.toFlowable
import timber.log.Timber
import java.io.File

class FirebaseStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val firestorage: FirebaseStorage,
    private val rxFirebaseStorage: IRxFireBaseStorage
) : IOnlineFileStorageRepository {

    private var storageRef = firestorage.reference

    /**
     * Saves all files associated with an [EncryptedEventEntity] (detail files, emotionavatar)
     */
    override fun saveEvent(encryptedEvent: EncryptedEvent): Completable {
        // TODO: do all the downloading in parallel using merge intead of concat
        return saveEventEmotionAvatar(encryptedEvent) // save avatar in firestorage
            .concatWith(
                encryptedEvent.details.toFlowable() // save all detail files in firestorage
                    .concatMapCompletable {
                        saveDetailFile(encryptedEvent, it)
                    }
            )
    }

    /**
     * Uploads the emotion avatar file to [firestorage]
     */
    private fun saveEventEmotionAvatar(encryptedEvent: EncryptedEvent): Completable {
        if (encryptedEvent.emotionAvatar == null) {
            return Completable.complete()
        } else {
            return rxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getEmotionAvatarPath(encryptedEvent).path),
                Uri.parse("file://${encryptedEvent.emotionAvatar!!.path}")
            ).doOnError {
                Timber.e(
                    "Problem saving emotionAvatar with path ${encryptedEvent.emotionAvatar}: ${it.localizedMessage}"
                )
            }.ignoreElement()
        }
    }

    /**
     * Uploads the detail file to [firestorage]
     */
    private fun saveDetailFile(event: EncryptedEvent, detail: EncryptedDetail): Completable {
        return rxFirebaseStorage.putFile(
            storageRef.child(pathProvider.getDetailPath(event, detail).path),
            Uri.parse("file://${detail.file.path}")
        ).doOnError {
            Timber.e(
                "Problems uploading detail file ${detail.file.path} : ${it.localizedMessage}"
            )
        }.ignoreElement()
    }

    /**
     * Downloads a detail's file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun downloadDetail(detail: Detail, event: Event): Completable {
        val fileToDownloadReference = storageRef.child(detail.file.path)
        val localFile: File = with(pathProvider) { localStoragePath(getDetailPath(detail, event)) }
        return Completable.fromSingle(rxFirebaseStorage.getFile(fileToDownloadReference, localFile))
            .andThen(Completable.fromCallable {
                detail.file = localFile
                Unit
            })
    }

    /**
     * Gets the emotion avatar file from [firestorage] and stores it into the path provided for it
     * by the [pathProvider].
     */
    override fun downloadEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null) {
            return Completable.complete()
        } else {
            return rxFirebaseStorage.getFile(
                storageRef.child(pathProvider.getEmotionAvatarPath(event).path),
                pathProvider.localStoragePath(event.emotionAvatar!!)
            )
                .flatMapCompletable {
                    Completable.fromCallable {
                        event.emotionAvatar = pathProvider.localStoragePath(event.emotionAvatar!!)
                        Unit
                    }
                }
        }
    }
}