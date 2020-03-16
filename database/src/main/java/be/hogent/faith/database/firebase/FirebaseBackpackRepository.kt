package be.hogent.faith.database.firebase

import be.hogent.faith.database.firebase.FirebaseEventRepository.Companion.USERS_KEY
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Flowable
import io.reactivex.Maybe
import timber.log.Timber

class FirebaseBackpackRepository (
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){

    fun insert(item: DetailEntity, user: UserEntity): Maybe<DetailEntity?> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != user.uuid) {
            return Maybe.error(RuntimeException("Unauthorized user."))
        } else {
            val document =
                firestore.collection(FirebaseUserRepository.USERS_KEY).document(currentUser.uid)
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

    fun get(): Flowable<List<DetailEntity>> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeQueryRef(
            firestore.collection(USERS_KEY).document(currentUser.uid).collection(
                BACKPACK
            )
        )
            .map {
                it.map { document ->
                    document.toObject(DetailEntity::class.java)
                }
            }
    }

    companion object {
        const val BACKPACK = "backpack"
    }
}