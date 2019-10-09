package be.hogent.faith.database.firebase

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

class FirebaseEventRepository {

    private val fbAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun get(uuid: UUID): Flowable<Event> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    fun insert(item: Event, user: User): Completable {
        return Completable.create { emitter ->
            val currentUser = fbAuth.currentUser
            if (currentUser == null) {
                Completable.error(RuntimeException("Unauthorized used."))
            } else {
                val db = firestore
                val collection = db.collection(FirebaseUserRepository.USERS_KEY)
                // explicitly set the document identifier
                collection.document(currentUser.uid).collection(EVENTS_KEY)
                    .document(item.uuid.toString()).set(item)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { e -> emitter.onError(RuntimeException("Fail to create event")) }
            }
        }
    }

    companion object {
        const val USERS_KEY = "users"
        const val EVENTS_KEY = "events"
    }
}