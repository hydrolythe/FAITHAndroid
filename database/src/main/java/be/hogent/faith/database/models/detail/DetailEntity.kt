package be.hogent.faith.database.models.detail

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import be.hogent.faith.database.models.EventEntity
import java.io.File
import java.util.UUID

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
abstract class DetailEntity(
    val file: File,

    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),

    val eventUuid: UUID
)