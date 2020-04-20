package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.util.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import timber.log.Timber

abstract class FirebaseDetailContainerRepository<T : DetailsContainer>(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    abstract val containerName: String

    open fun insert(item: DetailEntity, user: UserEntity): Maybe<DetailEntity?> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != user.uuid) {
            return Maybe.error(RuntimeException("Unauthorized user."))
        } else {
            val document =
                firestore.collection(FirebaseUserRepository.USERS_KEY).document(currentUser.uid)
                    .collection(containerName)
                    .document(item.uuid)
            return RxFirestore.setDocument(document, item)
                .andThen(
                    RxFirestore.getDocument(document)
                        .map { it.toObject(DetailEntity::class.java) })
                .onErrorResumeNext { error: Throwable ->
                    Timber.e("$TAG: error saving detail in $containerName ${error.message}")
                    Maybe.error(RuntimeException("Error saving detail in $containerName", error))
                }
        }
    }

    open fun get(): Flowable<List<DetailEntity>> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeQueryRef(
            firestore.collection(FirebaseEventRepository.USERS_KEY).document(currentUser.uid).collection(
                containerName
            )
        )
            .map {
                it.map { document ->
                    document.toObject(DetailEntity::class.java)
                }
            }
    }

    open fun delete(item: DetailEntity): Completable {
        val currentUser = fbAuth.currentUser
        return if (currentUser == null) {
            Completable.error(RuntimeException("User not set."))
        } else {
            RxFirestore.deleteDocument(
                firestore.collection(FirebaseEventRepository.USERS_KEY).document(
                    currentUser.uid
                ).collection(containerName).document(item.uuid)
            )
        }
    }
}