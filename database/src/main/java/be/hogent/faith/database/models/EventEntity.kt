package be.hogent.faith.database.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID
import com.google.firebase.Timestamp

data class EventEntity (
    val dateTime: String ="",
    val title: String= "",
    val emotionAvatar: String? = null,
    val notes: String? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val details : List<DetailsEntity> = emptyList()
){}