package be.hogent.faith.storage.firebase

import android.net.Uri
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import timber.log.Timber
import java.io.File

class FireBaseStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val firestorage: FirebaseStorage
) : IFireBaseStorageRepository {

    private var storageRef = firestorage.reference

     override fun saveEvent(event: Event): Single<Event> {
        return saveEventEmotionAvatar(event) // save avatar in firestorage
            .concatWith(
                event.details.toFlowable() // save all detail files in firestorage
                    .concatMapCompletable {
                        saveDetailFile(event, it)
                    }
            ).toSingle {event}
    }

    /**
     * Gets a detail's file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun getFile(detail: Detail): Completable{
        val fileToDownloadReference = storageRef.child(detail.file.path)
        val localFile: File = pathProvider.getLocalDetailPath(detail)
        return Completable.fromSingle(RxFirebaseStorage.getFile(fileToDownloadReference, localFile))

    }

     override fun getEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null)
            return Completable.complete()
        val fileToDownloadReference =
            storageRef.child(pathProvider.getEmotionAvatarPath(event).path)
        val localFile: File = pathProvider.getLocalEmotionAvatarPath(event)
        return Completable.fromSingle(RxFirebaseStorage.getFile(fileToDownloadReference, localFile))
    }

    private fun saveEventEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null)
            return Completable.complete()
        return Completable.fromSingle(
            RxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getEmotionAvatarPath(event).path),
                Uri.parse("file://${pathProvider.getLocalEmotionAvatarPath(event).path}")
            )
                .doOnError {
                    Timber.e(
                        "Firebase storage : Problems saving file ${pathProvider.getLocalEmotionAvatarPath(
                            event
                        ).path}: ${it.localizedMessage}"
                    )
                }
        )
    }

    private fun saveDetailFile(event: Event, detail: Detail): Completable {
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