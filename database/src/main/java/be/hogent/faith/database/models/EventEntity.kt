package be.hogent.faith.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

@Entity(
    tableName = "events",
    indices = [(Index(value = ["uuid"], unique = true))]
)
data class EventEntity(
    val dateTime: LocalDateTime,
    val title: String,
    val emotionAvatar: File?,
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID()
)