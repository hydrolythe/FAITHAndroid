package be.hogent.faith.database.detailcontainer

import be.hogent.faith.database.common.CONTAINERS_KEY
import be.hogent.faith.database.common.EncryptedDetailEntity
import be.hogent.faith.database.common.USERS_KEY
import be.hogent.faith.database.user.UserEntity
import be.hogent.faith.domain.models.detail.Detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import be.hogent.faith.database.rxfirebase3.RxFirestore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

abstract class DetailContainerDatabase<DetailContainer>(
    private val fbAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    abstract val containerName: String

    fun insertDetail(item: EncryptedDetailEntity, user: UserEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null || currentUser.uid != user.uuid) {
            return Completable.error(RuntimeException("Unauthorized user."))
        } else {
            val document = firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(containerName)
                .document(item.uuid)
            return RxFirestore.setDocument(document, item)
        }
    }

    fun getAll(): Flowable<List<EncryptedDetailEntity>> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.observeQueryRef(
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(containerName)
        )
            .map {
                it.map { document -> document.toObject(EncryptedDetailEntity::class.java) }
            }
    }

    fun delete(item: Detail): Completable {
        val currentUser = fbAuth.currentUser
        return if (currentUser == null) {
            Completable.error(RuntimeException("User not set."))
        } else {
            RxFirestore.deleteDocument(
                firestore
                    .collection(USERS_KEY)
                    .document(currentUser.uid)
                    .collection(containerName)
                    .document(item.uuid.toString())
            )
        }
    }

    fun insertContainer(container: EncryptedDetailsContainerEntity): Completable {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Completable.error(RuntimeException("Unauthorized user."))
        }
        val containerDocument = firestore
            .collection(USERS_KEY)
            .document(currentUser.uid)
            .collection(CONTAINERS_KEY)
            .document(containerName)
        return RxFirestore.setDocument(containerDocument, container)
    }

    fun getContainer(): Maybe<EncryptedDetailsContainerEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Maybe.error(RuntimeException("Unauthorized user."))
        }
        return RxFirestore.getDocument(
            firestore
                .collection(USERS_KEY)
                .document(currentUser.uid)
                .collection(CONTAINERS_KEY)
                .document(containerName)
        )
            .map { snapshot -> snapshot.toObject(EncryptedDetailsContainerEntity::class.java) }
    }
}