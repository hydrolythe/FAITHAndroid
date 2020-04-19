package be.hogent.faith.database.firebase

import be.hogent.faith.database.models.FilmEntity
import be.hogent.faith.domain.models.Cinema
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Flowable

class FirebaseCinemaRepository(
    fbAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : FirebaseDetailContainerRepository<Cinema>(fbAuth, firestore) {
    override val containerName: String = "cinema/details"
    val containerNameFilms: String = "cinema/films"

fun getFilms(): Flowable<List<FilmEntity>> {
    val currentUser = fbAuth.currentUser
    if (currentUser == null) {
        return Flowable.error(RuntimeException("Unauthorized user."))
    }
    return RxFirestore.observeQueryRef(
        firestore.collection(FirebaseEventRepository.USERS_KEY).document(currentUser.uid).collection(
            containerNameFilms
        )
    )
        .map {
            it.map { document ->
                document.toObject(FilmEntity::class.java)
            }
        }
}

fun deleteFilm(film: FilmEntity): Completable {
    val currentUser = fbAuth.currentUser
    return if (currentUser == null) {
        Completable.error(RuntimeException("User not set."))
    } else {
        RxFirestore.deleteDocument(
            firestore.collection(FirebaseEventRepository.USERS_KEY).document(
                currentUser.uid
            ).collection(containerNameFilms).document(film.uuid)
        )
    }
}
}