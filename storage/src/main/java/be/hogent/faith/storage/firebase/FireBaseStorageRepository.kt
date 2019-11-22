package be.hogent.faith.storage.firebase

import android.net.Uri
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.storage.StoragePathProvider
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import java.io.File

class FireBaseStorageRepository(
    private val pathProvider: StoragePathProvider,
    private val firestorage: FirebaseStorage
) : IStorageRepository {

    private var storageRef = firestorage.reference

    override fun saveEvent(event: Event): Completable {
        return saveEventEmotionAvatar(event) // save avatar in firestorage
            .andThen(
                event.details.toFlowable() // save all detail files in firestorage
                    .concatMapCompletable {
                        saveDetailFile(event, it)
                    }
            )
    }

    /**
     * Gets a detail's file from [firestorage] and stores it into the path provided for it by the
     * [pathProvider].
     */
    override fun getFile(detail: Detail): Single<File> {
        val fileToDownloadReference = storageRef.child(detail.file.path)
        val localFile: File = pathProvider.getLocalDetailPath(detail)
        return RxFirebaseStorage.getFile(fileToDownloadReference, localFile)
            .map { localFile }
    }

    override fun getEmotionAvatar(event: Event): Single<File> {
        if (event.emotionAvatar == null)
            return Single.just(null)
        val fileToDownloadReference =
            storageRef.child(pathProvider.getEmotionAvatarPath(event).path)
        val localFile: File = pathProvider.getLocalEmotionAvatarPath(event)
        return RxFirebaseStorage.getFile(fileToDownloadReference, localFile)
            .map { localFile }
    }

    fun saveEventEmotionAvatar(event: Event): Completable {
        if (event.emotionAvatar == null)
            return Completable.complete()
        return Completable.fromSingle(
            RxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getEmotionAvatarPath(event).path),
                Uri.parse("file://${pathProvider.getLocalEmotionAvatarPath(event).path}")
            )
        )
    }

    fun saveDetailFile(event: Event, detail: Detail): Completable {
        return Completable.fromSingle(
            RxFirebaseStorage.putFile(
                storageRef.child(pathProvider.getDetailPath(event, detail).path),
                Uri.parse("file://${pathProvider.getLocalDetailPath(detail).path}")
            )
        )
    }
}