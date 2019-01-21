package be.hogent.faith.database.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime
import java.util.UUID

@Entity(
    tableName = "events",
    indices = [(Index(value = ["uuid"], unique = true))]
)
internal data class EventEntity(
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),
    val date: LocalDateTime,
    val description: String
) {
    // Can't be part of the constructor arguments or Room won't compile
    @Ignore
    val details: MutableList<DetailEntity> = mutableListOf()
}