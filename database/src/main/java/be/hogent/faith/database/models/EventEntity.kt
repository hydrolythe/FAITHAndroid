package be.hogent.faith.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

@Entity(
    tableName = "events",
    indices = [(Index(value = ["uuid"], unique = true)), (Index(value = ["userUuid"], unique = false))],
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["uuid"],
        childColumns = ["userUuid"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EventEntity(
    val dateTime: LocalDateTime,
    val title: String,
    val emotionAvatar: File?,
    val notes: String?,

    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),

    val userUuid: UUID
)