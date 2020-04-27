package be.hogent.faith.database.detailcontainer

import be.hogent.faith.domain.models.Backpack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BackpackDatabase(
    fbAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : DetailContainerDatabase<Backpack>(fbAuth, firestore) {
    override val containerName: String = "backpack"
}