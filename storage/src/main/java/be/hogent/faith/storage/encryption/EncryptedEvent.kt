package be.hogent.faith.storage.encryption

import java.util.UUID

class EncryptedEvent(
    val dateTime: String,
    val title: String?,
    val emotionAvatar: String?,
    val notes: String?,
    val uuid: UUID
)
