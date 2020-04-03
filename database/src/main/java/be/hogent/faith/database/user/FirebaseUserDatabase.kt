package be.hogent.faith.database.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 *  The ruleset in Firestorage are set so a user can only CRUD it's own documents
 *  currentUser is the user who is logged in
 */
class FirebaseUserDatabase(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IUserDatabase {
    /**
     * get User for currentUser, observe changes
     */
    override fun get(uid: String): Flowable<UserEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != uid) {
            return Flowable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeDocumentRef(firestore.collection(USERS_KEY).document(currentUser.uid))
            .map { it.toObject(UserEntity::class.java) }
    }

    /**
     * Insert user in Firestorage in users/uuid
     */
    override fun insert(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != item.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        val document = firestore.collection(USERS_KEY).document(currentUser.uid)
        return RxFirestore.setDocument(document, item)
            .onErrorResumeNext {
                Completable.error(Throwable(java.lang.RuntimeException("Failed to create user")))
            }
    }

    /**
     * delete the user corresponding the currentUser in firestore
     */
    override fun delete(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != item.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.deleteDocument(firestore.collection(USERS_KEY).document(currentUser.uid))
    }

    companion object {
        const val USERS_KEY = "users"
    }
}