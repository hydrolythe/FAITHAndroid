package be.hogent.faith.database.goal

import be.hogent.faith.service.encryption.EncryptedString
import java.util.UUID

data class EncryptedGoalEntity(
    val dateTime: String = "",
    val description: String = "",
    val uuid: String = UUID.randomUUID().toString(),
    val isCompleted: Boolean,
    val encryptedDEK: EncryptedString = ""
)
