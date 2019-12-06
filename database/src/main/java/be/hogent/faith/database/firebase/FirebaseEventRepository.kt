package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Flowable
import io.reactivex.Maybe
import timber.log.Timber

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
    fun get(eventUid: String): Flowable<EventEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeDocumentRef(
            firestore.collection(USERS_KEY).document(currentUser.uid).collection(EVENTS_KEY)
                .document(eventUid)
        )
            .map { it.toObject(EventEntity::class.java) }
    }

    /**
     * get all events for current authenticated user, also listens for changes
     */
    fun getAll(): Flowable<List<EventEntity>> {
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
            return RxFirestore.setDocument(document, item) // ) // stores the event in firestore
                .andThen(RxFirestore.getDocument(document) // gets the just stored event from firestore
                    .map { it.toObject(EventEntity::class.java) })
                .onErrorResumeNext { error: Throwable ->
                    Timber.e("$TAG: error saving event ${error.message}")
                    Maybe.error(RuntimeException("Error saving event", error))
                }
        }
    }

    companion object {
        const val USERS_KEY = "users"
        const val EVENTS_KEY = "events"
    }
}