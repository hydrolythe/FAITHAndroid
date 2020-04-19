package be.hogent.faith.database.firebase

import be.hogent.faith.domain.models.Cinema
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCinemaRepository(
    fbAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : FirebaseDetailContainerRepository<Cinema>(fbAuth, firestore) {
    override val containerName: String = "cinema"
}