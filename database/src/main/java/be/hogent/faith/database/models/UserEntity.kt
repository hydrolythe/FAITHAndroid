package be.hogent.faith.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "users",
    indices = [(Index(value = ["uuid"], unique = true))]
)
data class UserEntity(
    @PrimaryKey
    val uuid: UUID = UUID.randomUUID(),
    val username: String,
    val avatarName: String
)