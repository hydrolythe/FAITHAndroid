package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import timber.log.Timber

class FirebaseBackpackRepository(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun insert(item: DetailEntity, user: UserEntity): Maybe<DetailEntity?> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != user.uuid) {
            return Maybe.error(RuntimeException("Unauthorized user."))
        } else {
            val document =
                firestore.collection(FirebaseUserDatabase.USERS_KEY).document(currentUser.uid)
                    .collection(BACKPACK)
                    .document(item.uuid)
            return RxFirestore.setDocument(document, item)
                .andThen(RxFirestore.getDocument(document)
                    .map { it.toObject(DetailEntity::class.java) })
                .onErrorResumeNext { error: Throwable ->
                    Timber.e("$TAG: error saving detail in backpack ${error.message}")
                    Maybe.error(RuntimeException("Error saving detail in backpack", error))
                }
        }
    }

    fun get(): Observable<List<DetailEntity>> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Observable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeQueryRef(
            firestore.collection(FirebaseUserDatabase.USERS_KEY).document(currentUser.uid)
                .collection(BACKPACK)
        )
            .map {
                it.map { document -> document.toObject(DetailEntity::class.java) }
            }
            .toObservable()
    }

    fun delete(item: DetailEntity): Completable {
        val currentUser = fbAuth.currentUser
        return if (currentUser == null) {
            Completable.error(RuntimeException("User not set."))
        } else {
            RxFirestore.deleteDocument(
                firestore.collection(FirebaseUserDatabase.USERS_KEY).document(currentUser.uid)
                    .collection(BACKPACK)
                    .document(item.uuid)
            )
        }
    }

    companion object {
        const val BACKPACK = "backpack"
    }
}