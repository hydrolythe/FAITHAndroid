package be.hogent.faith.storage.firebase

import android.net.Uri
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import timber.log.Timber
import java.io.File

class OnlineStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val firestorage: FirebaseStorage
) : IOnlineStorageRepository {

    private var storageRef = firestorage.reference

    /**
     * Saves all files associated with an [EncryptedEventEntity] (detail files, emotionavatar)
     */
    override fun saveEvent(encryptedEventEntity: EncryptedEventEntity): Completable {
        return saveEventEmotionAvatar(encryptedEventEntity) // save avatar in firestorage
            .concatWith(
                encryptedEventEntity.detailEntities.toFlowable() // save all detail files in firestorage
                    .concatMapCompletable {
                        saveDetailFile(encryptedEventEntity, it)
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
    private fun saveEventEmotionAvatar(encryptedEventEntity: EncryptedEventEntity): Completable {
        if (encryptedEventEntity.emotionAvatar == null)
            return Completable.complete()
        return Completable.fromSingle(
            RxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getEmotionAvatarPath(encryptedEventEntity).path),
                Uri.parse("file://${pathProvider.getLocalEmotionAvatarPath(encryptedEventEntity).path}")
            )
                .doOnError {
                    Timber.e(
                        "Firebase storage : Problems saving file ${pathProvider.getLocalEmotionAvatarPath(
                            encryptedEventEntity
                        ).path}: ${it.localizedMessage}"
                    )
                }
        )
    }

    /**
     * Uploads the detail file to [firestorage]
     */
    private fun saveDetailFile(
        event: EncryptedEventEntity,
        detail: EncryptedDetailEntity
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