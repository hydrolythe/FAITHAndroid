package be.hogent.faith.storage.firebase

import android.net.Uri
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.rxkotlin.toFlowable
import timber.log.Timber
import java.io.File

class OnlineFileStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val firestorage: FirebaseStorage
) : IOnlineFileStorageRepository {

    private var storageRef = firestorage.reference

    /**
     * Saves all files associated with an [EncryptedEventEntity] (detail files, emotionavatar)
     */
    override fun saveEvent(encryptedEvent: EncryptedEvent): Completable {
        return saveEventEmotionAvatar(encryptedEvent) // save avatar in firestorage
            .concatWith(
                encryptedEvent.details.toFlowable() // save all detail files in firestorage
                    .concatMapCompletable {
                        saveDetailFile(encryptedEvent, it)
                    }
            )
    }

    /**
     * Gets a detail's file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun downloadDetail(detail: Detail): Completable {
        val fileToDownloadReference = storageRef.child(detail.file.path)
        val localFile: File = pathProvider.getLocalDetailPath(detail)
        return Completable.fromSingle(RxFirebaseStorage.getFile(fileToDownloadReference, localFile))
    }

    /**
     * Gets the emotion avatar file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun downloadEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null)
            return Completable.complete()
        val fileToDownloadReference =
            storageRef.child(pathProvider.getEmotionAvatarPath(event).path)
        val localFile: File = pathProvider.getLocalEmotionAvatarPath(event)
        return Completable.fromSingle(RxFirebaseStorage.getFile(fileToDownloadReference, localFile))
    }

    /**
     * Uploads the emotion avatar file to [firestorage]
     */
    private fun saveEventEmotionAvatar(encryptedEvent: EncryptedEvent): Completable {
        if (encryptedEvent.emotionAvatar == null)
            return Completable.complete()
        return Completable.fromSingle(
            RxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getEmotionAvatarPath(encryptedEvent).path),
                Uri.parse("file://${pathProvider.getLocalEmotionAvatarPath(encryptedEvent).path}")
            )
                .doOnError {
                    Timber.e(
                        "Firebase storage : Problems saving file ${pathProvider.getLocalEmotionAvatarPath(
                            encryptedEvent
                        ).path}: ${it.localizedMessage}"
                    )
                }
        )
    }

    /**
     * Uploads the detail file to [firestorage]
     */
    private fun saveDetailFile(
        event: EncryptedEvent,
        detail: EncryptedDetail
    ): Completable {
        return Completable.fromSingle(
            RxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getDetailPath(event, detail).path),
                Uri.parse("file://${pathProvider.getLocalDetailPath(detail).path}")
            ).doOnError {
                Timber.e(
                    "Firebase storage : Problems saving file ${pathProvider.getLocalEmotionAvatarPath(
                        event
                    ).path} : ${it.localizedMessage}"
                )
            }
        )
    }
}