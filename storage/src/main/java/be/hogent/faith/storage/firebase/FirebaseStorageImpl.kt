package be.hogent.faith.storage.firebase

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.lang.RuntimeException


class FirebaseStorageImpl(
    private val fbAuth: FirebaseAuth,
    private val firestorage: FirebaseStorage
) : IFirebaseStorage {

    private val currentUser: FirebaseUser?
        get() = fbAuth.currentUser

    private fun getStorageEventReference(event: Event) =
        firestorage.reference.child(USERS_KEY).child(fbAuth.currentUser!!.uid)
            .child(EVENTS_KEY).child(event.uuid.toString())

    override fun saveEventEmotionAvatar(event: Event): Single<File> {
        Log.d(TAG, "saving event : saves emotionavatar ${event.emotionAvatar?.path}")
        if (currentUser == null)
            return Single.error(RuntimeException("not authorized"))
        if (event.emotionAvatar == null)
            return Single.just(null)
        return RxFirebaseStorage.putFile(
            getStorageEventReference(event).child(
                "avatar.png"
            ),
            Uri.parse("file://" + event.emotionAvatar)
        )
            .map { File(it.storage.path) }
    }

    override fun saveDetailFile(event: Event, detail: Detail): Single<File> {
        if (currentUser == null)
            return Single.error(RuntimeException("not authorized"))
        val fileExt = MimeTypeMap.getFileExtensionFromUrl(detail.file.path)
        return RxFirebaseStorage.putFile(
            getStorageEventReference(event).child(
                "${detail.uuid}.$fileExt"
            ),
            Uri.parse("file://" + detail.file)
        )
            .map { File(it.storage.path) }
    }

    override fun moveFilesFromRemoteStorageToLocalStorage(event: Event): Completable {
        if (currentUser == null)
            return Completable.error(RuntimeException("not authorized"))
        //download all files for the event if not yet in local storage
        getStorageEventReference(event).listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { item ->

                //eerst controleren of local file bestaat. Indien niet dan downloaden van firestorage
                // All the items-storagereference under listRef.
                val localFile =
                    File.createTempFile(
                        "images",
                        "jpg"
                    ) // the local file where it will be saved
                //download the file to local storage
                item.getFile(localFile)
                    .addOnSuccessListener {
                        // Local temp file has been created
                    }
                    .addOnFailureListener {
                        Completable.error(RuntimeException(it.localizedMessage))
                    }
            }
        }

            .addOnFailureListener {
                Completable.error(RuntimeException(it.localizedMessage))
            }
        return Completable.complete()
        TODO("save to local storage")
    }


    companion object {
        const val USERS_KEY = "users"
        const val EVENTS_KEY = "events"
    }
}