package be.hogent.faith.database.firebase


import be.hogent.faith.database.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class FirebaseUserRepository  {

    private val fbAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

     fun insert(item: UserEntity): Completable {
        return Completable.create { emitter ->
            val currentUser = fbAuth.currentUser
            if (currentUser == null) {
                Completable.error(RuntimeException("Unauthorized used."))
            } else {
                val db = firestore
                val collection = db.collection(USERS_KEY)
                //explicitly set the document identifier
                collection.document(currentUser.uid).set(item)
                        .addOnSuccessListener { emitter.onComplete() }
                        .addOnFailureListener { e -> emitter.onError(RuntimeException("Fail to create user")) }
            }
        }
    }


     fun getAll(): Flowable<List<UserEntity>> {
        return Flowable.create({ emitter ->
            firestore.collection(USERS_KEY)
                .get()
                //.addSnapShotListener als je realtime updates wenst
                .addOnSuccessListener { snapshot ->
                    val users = snapshot?.map { document ->
                        document.toObject(UserEntity::class.java)}
                    users?.let { emitter.onNext(it) }
                }
                .addOnFailureListener { exception ->
                    emitter.onError(exception)
                }
        }, BackpressureStrategy.LATEST)
    }


     fun delete(item: UserEntity): Completable {
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

     fun isUsernameUnique(username:String): Single<Boolean> {
         return Single.create{ emitter ->
             firestore.collection(USERS_KEY)
                 .whereEqualTo("username", username)
                 .get()
                 .addOnSuccessListener { snapshot ->
                     if (snapshot.isEmpty)
                         emitter.onSuccess(false)
                     else
                         emitter.onSuccess(true)
                 }
                 .addOnFailureListener { e ->
                     emitter.onError(e)
                 }
         }
     }

     fun get(uuid: String): Flowable<UserEntity> {
        val currentUser = fbAuth.currentUser
        if (currentUser == null) {
            return Flowable.error(RuntimeException("Unauthorized used."))
        }
        return Flowable.create({ emitter ->
            firestore.collection(USERS_KEY)
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot?.toObject(UserEntity::class.java)
                    user?.let {
                        emitter.onNext(it)
                    }
                }
                .addOnFailureListener { e ->
                        emitter.onError(e)
                  }
        },BackpressureStrategy.LATEST)
    }

    companion object {
        const val USERS_KEY = "users"
        const val USER_ID_KEY = "uuid"
    }
}