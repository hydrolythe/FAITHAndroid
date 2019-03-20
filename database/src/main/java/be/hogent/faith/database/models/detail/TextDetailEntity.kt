package be.hogent.faith.database.models.detail

import androidx.room.Entity
import java.io.File
import androidx.room.ForeignKey
import androidx.room.Index
import be.hogent.faith.database.models.EventEntity
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
class TextDetailEntity(
    file: File,
    uuid: UUID = UUID.randomUUID(),
    eventUuid: UUID
) : DetailEntity(file, uuid, eventUuid)