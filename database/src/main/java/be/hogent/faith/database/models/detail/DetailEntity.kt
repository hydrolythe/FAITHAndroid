package be.hogent.faith.database.models.detail

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.util.UUID

// The relationships are defined separately in each subclass
// The reason why we need separate tables for each subtype is explained in the [DetailDao].
@Entity
abstract class DetailEntity(
    val file: File,
    val name: String? = null,
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),

    val eventUuid: UUID
)