package be.hogent.faith.database.firebase

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import be.hogent.faith.database.firebase.FirebaseUserRepository.Companion.USERS_KEY
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import durdinapps.rxfirebase2.RxFirebaseStorage
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable

/**
 * uses RxFirebase: https://github.com/FrangSierra/RxFirebase
 * document hierarchy in Firestore : users/{userUuid}/projects/{projectUuid}.
 * storage hierarchy in Firestorage : idem
 * the ruleset on both storages is so that a user can only CRUD his documents
 */
class FirebaseEventRepository(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firestorage: FirebaseStorage
) {

    private val storageRef: StorageReference
        get() = firestorage.reference.child(USERS_KEY)

    /**
     * gets event with uuid for the current user, also listens for changes
     */
    fun get(uuid: String): Flowable<EventEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized used."))
        }
        return RxFirestore.observeDocumentRef(
            firestore.collection(USERS_KEY).document(currentUser.uid).collection(EVENTS_KEY)
                .document(uuid)
        )
            .map { it.toObject(EventEntity::class.java) }
    }

    /**
     * gets all events for current user, also listens for changes
     */
    fun getAll(): Flowable<List<EventEntity>> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized used."))
        }
        return RxFirestore.observeQueryRef(
            firestore.collection(USERS_KEY).document(currentUser.uid).collection(
                EVENTS_KEY
            )
        )
            .map {
                it.map { document ->
                    document.toObject(EventEntity::class.java)
                }
            }
    }

    /**
     * Inserting an event in 3 steps
     * 1. saving the emotionAvatar file and update the file reference
     * 2. for each detail : save the corresponding file and update the file reference
     * 3. insert the event
     */
    fun insert(item: EventEntity, user: UserEntity): Maybe<EventEntity?> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != user.uuid) {
            return Maybe.error(RuntimeException("Unauthorized user."))
        } else {
            // sets the document reference for saving the event in firestore
            val document =
                firestore.collection(FirebaseUserRepository.USERS_KEY).document(currentUser.uid)
                    .collection(EVENTS_KEY)
                    .document(item.uuid)
            return Completable.fromSingle(saveAvatarForEvent(item)) // save avatar in firestorage
                .andThen(
                    Completable.fromPublisher(item.details.toFlowable() // save all detail files in firestorage
                        .concatMapSingle {
                            saveFileForEventDetail(item.uuid, it)
                        }
                    ))
                .andThen(RxFirestore.setDocument(document, item)) // stores the event in firestore
                .andThen(RxFirestore.getDocument(document) // gets the just stored event from firestore
                    .map { it.toObject(EventEntity::class.java) })
                .onErrorResumeNext { error: Throwable ->
                    Log.e(TAG, "error saving event ${error.message}")
                    Maybe.error(RuntimeException("Error saving event", error))
                }
        }
    }

    /**
     * Save the avatar in storage and update the file path
     */
    fun saveAvatarForEvent(item: EventEntity): Single<String> {
        return RxFirebaseStorage.putFile(
            storageRef.child(fbAuth.currentUser!!.uid).child(EVENTS_KEY).child(item.uuid).child(
                "avatar.png"
            ),
            Uri.parse("file://" + item.emotionAvatar)
        )
            .map { it.storage.path } // GlideApp werkt met references, betere UX (File(it.storage.downloadUrl.toString())
            .doOnSuccess { storedFile ->
                Log.i(ContentValues.TAG, "emotion avatar file is saved $storedFile")
                item.emotionAvatar = storedFile
            }
    }

    /**
     * Save the file for each detail in storage and update the file path
     */
    fun saveFileForEventDetail(
        projectUuid: String,
        detail: DetailEntity
    ): Single<String> {
        return RxFirebaseStorage.putFile(
            storageRef.child(fbAuth.currentUser!!.uid).child(EVENTS_KEY).child(projectUuid.toString()).child(
                "${detail.uuid}.png"
            ),
            Uri.parse("file://" + detail.file)
        )
            .map { it.storage.path }
            .doOnSuccess { detail.file = it }
    }

    companion object {
        const val USERS_KEY = "users"
        const val EVENTS_KEY = "events"
    }
}