package be.hogent.faith.database.models.detail

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.util.UUID

@Entity
abstract class DetailEntity(
    val file: File,
    val name: String? = null,
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),

    val eventUuid: UUID
)