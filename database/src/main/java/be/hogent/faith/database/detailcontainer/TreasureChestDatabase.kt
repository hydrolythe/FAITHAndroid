package be.hogent.faith.database.detailcontainer

import be.hogent.faith.domain.models.TreasureChest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TreasureChestDatabase(
    fbAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : DetailContainerDatabase<TreasureChest>(fbAuth, firestore) {
    override val containerName: String = "treasurechest"
}