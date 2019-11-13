package be.hogent.faith.storage.firebase

import android.net.Uri
import android.webkit.MimeTypeMap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File


class FirebaseStorageImpl(
    private val fbAuth: FirebaseAuth,
    private val firestorage: FirebaseStorage
) : IFirebaseStorage {
    override fun saveEventEmotionAvatar(event: Event): Single<File> {
        if (event.emotionAvatar == null)
            return Single.just(null)
        return RxFirebaseStorage.putFile(
            storageRef.child(fbAuth.currentUser!!.uid).child(EVENTS_KEY).child(event.uuid.toString()).child(
                "avatar.png"
            ),
            Uri.parse("file://" + event.emotionAvatar)
        )
            .map { File(it.storage.path) } // GlideApp werkt met references, betere UX (File(it.storage.downloadUrl.toString())
        //  .doOnSuccess { storedFile ->
        //      item.emotionAvatar = storedFile
        //  }
    }

    override fun saveDetailFile(event: Event, detail: Detail): Single<File> {
        val fileExt = MimeTypeMap.getFileExtensionFromUrl(detail.file.path)
        return RxFirebaseStorage.putFile(
            storageRef.child(fbAuth.currentUser!!.uid).child(EVENTS_KEY).child(event.uuid.toString()).child(
                "${detail.uuid}.$fileExt"
            ),
            Uri.parse("file://" + detail.file)
        )
            .map { File(it.storage.path) }
        // .doOnSuccess { detail.file = File(it) }
    }

    override fun moveFilesFromRemoteStorageToCacheStorage(event: Event): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val storageRef: StorageReference
        get() = firestorage.reference.child(USERS_KEY)

    private val currentUser: FirebaseUser?
        get() = fbAuth.currentUser


    companion object {
        const val USERS_KEY = "users"
        const val EVENTS_KEY = "events"
    }
}