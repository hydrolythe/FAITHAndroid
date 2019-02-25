package be.hogent.faith.database.models

import androidx.room.Entity
import androidx.room.Ignore
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
) {
    // Can't be part of the constructor arguments or Room won't compile
    @Ignore
    val details: MutableList<DetailEntity> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventEntity

        if (dateTime != other.dateTime) return false
        if (title != other.title) return false
        if (emotionAvatar != other.emotionAvatar) return false
        if (uuid != other.uuid) return false
        if (details != other.details) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateTime.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (emotionAvatar?.hashCode() ?: 0)
        result = 31 * result + uuid.hashCode()
        result = 31 * result + details.hashCode()
        return result
    }
}