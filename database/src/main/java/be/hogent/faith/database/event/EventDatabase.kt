package be.hogent.faith.database.event

import be.hogent.faith.database.common.EVENTS_KEY
import be.hogent.faith.database.common.USERS_KEY
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.util.UUID

/**
 * uses RxFirebase: https://github.com/FrangSierra/RxFirebase
 * document hierarchy in Firestore : users/{userUid}/events/{eventUuid}.
 * storage hierarchy in Firestorage : idem
 * the ruleset on both storages is so that a user can only CRUD his documents
 */
class EventDatabase(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * get event with eventUid for the current authenticated user, also listens for changes
     */
    fun get(eventUuid: UUID): Flowable<EncryptedEventEntity> {
        val currentUser = fbAuth.currentUser
            ?: return Flowable.error(RuntimeException("Unauthorized user."))
        return RxFirestore.observeDocumentRef(
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(EVENTS_KEY)
                .document(eventUuid.toString())
        )
            .map { it.toObject(EncryptedEventEntity::class.java) }
    }

    /**
     * get all events for current authenticated user, also listens for changes
     */
    fun getAll(): Flowable<List<EncryptedEventEntity>> {
        val currentUser =
            fbAuth.currentUser ?: return Flowable.error(RuntimeException("Unauthorized user."))
        return RxFirestore.observeQueryRef(
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(EVENTS_KEY)
        )
            .map {
                it.map { document -> document.toObject(EncryptedEventEntity::class.java) }
            }
    }

    fun insert(item: EncryptedEventEntity): Completable {
        val currentUser = fbAuth.currentUser
            ?: return Completable.error(RuntimeException("Unauthorized user."))
        val document =
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(EVENTS_KEY)
                .document(item.uuid)
        return RxFirestore.setDocument(document, item)
    }

    fun delete(event: Event, user: User): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser!!.uid != user.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        val document =
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(EVENTS_KEY)
                .document(event.uuid.toString())
        return RxFirestore.deleteDocument(document)
            .onErrorComplete()
    }

    fun update(item: EncryptedEventEntity): Completable {
        // insert overwrites by default
        return insert(item)
    }
}