package be.hogent.faith.database.detailcontainer

import be.hogent.faith.domain.models.Cinema
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CinemaDatabase(
    fbAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : DetailContainerDatabase<Cinema>(fbAuth, firestore) {
    override val containerName: String = "cinema"
}