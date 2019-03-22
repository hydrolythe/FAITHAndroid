package be.hogent.faith.database.models.detail

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import be.hogent.faith.database.models.EventEntity
import java.io.File
import java.util.UUID

abstract class DetailEntity(
    val file: File,
    val name: String? = null,
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),

    val eventUuid: UUID
)