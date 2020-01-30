package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.database.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * uses RxFirebase: https://github.com/FrangSierra/RxFirebase
 * document hierarchy in Firestore : users/{userUid}/events/{eventUuid}.
 * storage hierarchy in Firestorage : idem
 * the ruleset on both storages is so that a user can only CRUD his documents
 */
class FirebaseEventRepository(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * get event with eventUid for the current authenticated user, also listens for changes
     */
    fun get(eventUid: String): Observable<EncryptedEventEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Observable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeDocumentRef(
            firestore.collection(USERS_KEY).document(currentUser.uid).collection(EVENTS_KEY)
                .document(eventUid)
        )
            .toObservable() // No need for a Flowable as updates are infrequent
            .map { it.toObject(EncryptedEventEntity::class.java) }
    }

    /**
     * get all events for current authenticated user, also listens for changes
     */
    fun getAll(): Flowable<List<EncryptedEventEntity>> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeQueryRef(
            firestore.collection(USERS_KEY).document(currentUser.uid).collection(
                EVENTS_KEY
            )
        )
            .map {
                it.map { document ->
                    document.toObject(EncryptedEventEntity::class.java)
                }
            }
    }

    /**
     * Inserting an event in 3 steps
     * 1. saving the emotionAvatar file and update the file reference
     * 2. for each detail : save the corresponding file and update the file reference
     * 3. insert the event
     */
    fun insert(item: EncryptedEventEntity, user: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != user.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        } else {
            // sets the document reference for saving the event in firestore
            val document =
                firestore.collection(FirebaseUserRepository.USERS_KEY).document(currentUser.uid)
                    .collection(EVENTS_KEY)
                    .document(item.uuid)
            return RxFirestore.setDocument(document, item) // ) // stores the event in firestore
        }
    }

    companion object {
        const val USERS_KEY = "users"
        const val EVENTS_KEY = "events"
    }
}