package be.hogent.faith.database.models

import androidx.room.*
import be.hogent.faith.database.converters.DetailTypeConverter
import be.hogent.faith.database.converters.UuidConverter
import java.util.*

@Entity(
    tableName = "details",
    indices = [(Index(value = ["uuid"], unique = true)),
        (Index(value = ["eventUuid"], unique = false))],
    foreignKeys = [ForeignKey(
        entity = EventEntity::class,
        parentColumns = ["uuid"],
        childColumns = ["eventUuid"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DetailEntity(
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),

    val eventUuid: UUID,

    val type: DetailTypeEntity
)