package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 *  The ruleset in Firestorage are set so a user can only CRUD it's own documents
 *  currentUser is the user who is logged in
 */
class FirebaseUserRepository(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    /**
     * get User for currentUser, observe changes
     */
    fun get(uid: String): Flowable<UserEntity> {
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
    fun insert(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != item.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        val document = firestore.collection(USERS_KEY).document(currentUser.uid)
        return RxFirestore.setDocument(document, item)
            .onErrorResumeNext {
                Completable.error(
                    Throwable(
                        java.lang.RuntimeException(
                            "Failed to create user"
                        )
                    )
                )
            }
    }

    /**
     * delete the user corresponding the currentUser in firestore
     */
    fun delete(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != item.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.deleteDocument(firestore.collection(USERS_KEY).document(currentUser.uid))
    }

    /**
     * get all users from Firestorage
     */
    fun getAll(): Flowable<List<UserEntity>> {
        TODO("not implemented, because rules for an admin in firestore not set") // if used, set the rules on the database
        /*
        return RxFirestore.observeQueryRef(firestore.collection(USERS_KEY))
            .map {
                it?.map { document ->
                    document.toObject(UserEntity::class.java)
                }
            }
         */
    }

    companion object {
        const val USERS_KEY = "users"
        const val USER_ID_KEY = "uuid"
    }
}