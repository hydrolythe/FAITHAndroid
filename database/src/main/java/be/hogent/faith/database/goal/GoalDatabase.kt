package be.hogent.faith.database.goal

import be.hogent.faith.database.common.GOALS_KEY
import be.hogent.faith.database.common.USERS_KEY
import be.hogent.faith.database.rxfirebase3.RxFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.util.UUID

/**
 * uses RxFirebase: https://github.com/FrangSierra/RxFirebase
 * document hierarchy in Firestore : users/{userUid}/events/{eventUuid}.
 * the ruleset on both storages is so that a user can only CRUD his documents
 */
class GoalDatabase(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * get goal with goalUid for the current authenticated user, also listens for changes
     */
    fun get(goalUuid: UUID): Flowable<EncryptedGoalEntity> {
        val currentUser = fbAuth.currentUser
            ?: return Flowable.error(RuntimeException("Unauthorized user."))
        return RxFirestore.observeDocumentRef(
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(GOALS_KEY)
                .document(goalUuid.toString())
        )
            .map { it.toObject(EncryptedGoalEntity::class.java) }
    }

    /**
     * get all goals for current authenticated user, also listens for changes
     */
    fun getAll(): Flowable<List<EncryptedGoalEntity>> {
        val currentUser = fbAuth.currentUser
            ?: return Flowable.error(RuntimeException("Unauthorized user."))
        return RxFirestore.observeQueryRef(
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(GOALS_KEY)
        )
            .map {
                it.map { document -> document.toObject(EncryptedGoalEntity::class.java) }
            }
    }

    /**
     * insert goals for current authenticated user
     */
    fun insert(item: EncryptedGoalEntity): Completable {
        val currentUser = fbAuth.currentUser
            ?: return Completable.error(RuntimeException("Unauthorized user."))
        // sets the document reference for saving the goal in firestore
        val document =
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(GOALS_KEY)
                .document(item.uuid)
        return RxFirestore.setDocument(document, item) // ) // stores the goal in firestore
    }

    /**
     * update goals for current authenticated user
     */
    fun update(item: EncryptedGoalEntity): Completable {
        // insert overwrites by default
        return insert(item)
    }

    /**
     * delete goal for current authenticated user
     */
    fun delete(goalUuid: UUID): Completable {
        val currentUser = fbAuth.currentUser
            ?: return Completable.error(RuntimeException("Unauthorized user."))
        // sets the document reference for saving the goal in firestore
        val document =
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(GOALS_KEY)
                .document(goalUuid.toString())
        return RxFirestore.deleteDocument(document)
    }
}