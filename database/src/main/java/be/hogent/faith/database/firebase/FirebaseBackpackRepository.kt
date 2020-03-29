package be.hogent.faith.database.firebase

import be.hogent.faith.domain.models.Backpack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseBackpackRepository(
      fbAuth: FirebaseAuth,
      firestore: FirebaseFirestore
) : FirebaseDetailContainerRepository<Backpack>(fbAuth, firestore) {
    override val containerName: String = "backpack"
}