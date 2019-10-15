package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable

class FirebaseUserRepository(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun insert(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Completable.error(RuntimeException("Unauthorized used."))
        }
        val document = firestore.collection(USERS_KEY).document(currentUser.uid)
        return RxFirestore.setDocument(document, item)
            .onErrorResumeNext { error: Throwable ->
                Completable.error(
                    Throwable(
                        java.lang.RuntimeException(
                            "Failed to create user"
                        )
                    )
                )
            }
    }

    fun getAll(): Flowable<List<UserEntity>> {
        return RxFirestore.observeQueryRef(firestore.collection(USERS_KEY))
            .map {
                it?.map { document ->
                    document.toObject(UserEntity::class.java)
                }
            }
    }

    fun delete(item: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Completable.error(RuntimeException("Unauthorized used."))
        }
        return RxFirestore.deleteDocument(firestore.collection(USERS_KEY).document(currentUser.uid))
    }

    fun get(uuid: String): Flowable<UserEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized used."))
        }
        return RxFirestore.observeDocumentRef(firestore.collection(USERS_KEY).document(currentUser.uid))
            .map { it?.toObject(UserEntity::class.java) }
    }

    companion object {
        const val USERS_KEY = "users"
        const val USER_ID_KEY = "uuid"
    }
}