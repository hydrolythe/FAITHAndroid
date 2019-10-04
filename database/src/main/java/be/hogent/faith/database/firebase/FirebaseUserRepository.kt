package be.hogent.faith.database.firebase

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

class FirebaseUserRepository : UserRepository {

    private val fbAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun insert(user: User): Completable {
        return Completable.create { emitter ->
            val currentUser = fbAuth.currentUser
            if (currentUser == null) {
                Completable.error(RuntimeException("Unauthorized used."))
            } else {
                val db = firestore
                val collection = db.collection(USERS_KEY)
                val saveTask =
                    collection.document(currentUser.uid).set(user)
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener { e -> emitter.onError(e) }
            }
        }
    }

    override fun getAll(): Flowable<List<User>> {
        //niet nodig????
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(user: User): Completable {
        return Completable.create { emitter ->
            val db = firestore
            val currentUser = fbAuth.currentUser
            if (currentUser == null) {
                Completable.error(RuntimeException("Unauthorized used."))
            } else {
                db.collection(USERS_KEY)
                    .document(currentUser.uid)
                    .delete()
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }
                    .addOnFailureListener { e ->
                        emitter.onError(e)
                    }
            }
        }
    }

    override fun get(uuid: UUID): Flowable<User> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized used."))
        }
        return Flowable.create({ emitter ->
            firestore.collection(USERS_KEY)
                .document(currentUser.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val user = snapshot?.toObject(User::class.java)
                        user?.let { emitter.onNext(it) }
                    } else {
                        emitter.onError(e)
                    }
                }
        }, BackpressureStrategy.LATEST)
    }


    companion object {
        const val USERS_KEY = "users"
        const val USER_ID_KEY = "uuid"
    }
}