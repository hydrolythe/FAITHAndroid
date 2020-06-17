package be.hogent.faith.database.user

import be.hogent.faith.database.common.USERS_KEY
import be.hogent.faith.rxfirebase3.RxFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

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
     * Set user in Firestorage in users/uuid
     */
    override fun initialiseUser(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != item.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        val document = firestore.collection(USERS_KEY).document(currentUser.uid)
        return RxFirestore.setDocument(document, item)
            .onErrorResumeNext {
                Completable.error(Throwable(java.lang.RuntimeException("Failed to initialize user")))
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
}