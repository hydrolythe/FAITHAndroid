package be.hogent.faith.database.event

import be.hogent.faith.database.common.EncryptedDetailEntity
import be.hogent.faith.service.encryption.EncryptedString
import java.util.UUID

data class EncryptedEventEntity(
    val dateTime: String = "",
    val title: String = "",
    var emotionAvatar: String? = null,
    var emotionAvatarThumbnail: String? = null,
    val notes: String? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val details: List<EncryptedDetailEntity> = emptyList(),
    val encryptedDEK: EncryptedString = "",
    val encryptedStreamingDEK: EncryptedString = ""
)
