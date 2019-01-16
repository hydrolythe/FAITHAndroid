package be.hogent.faith.database.models

import androidx.room.*
import org.threeten.bp.LocalDateTime
import java.util.*

@Entity(
    tableName = "events",
    indices = [(Index(value = ["uuid"], unique = true))]
)
data class EventEntity(
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),
    val date: LocalDateTime,
    val description: String
) {
    //Can't be part of the constructor arguments or Room won't compile
    @Ignore
    val details: MutableList<DetailEntity> = mutableListOf()
}