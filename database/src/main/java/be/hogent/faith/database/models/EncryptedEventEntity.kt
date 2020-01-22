package be.hogent.faith.database.models

import java.util.UUID

data class EncryptedEventEntity(
    val dateTime: String = "",
    val title: String = "",
    var emotionAvatar: String? = null,
    val notes: String? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val details: List<EncryptedDetailEntity> = emptyList(),
    /**
     * @see [be.hogent.faith.database.encryption.EncryptedEventInterface.encryptedDEK]
     */
    val encryptedDEK: String,
    /**
     * @see [be.hogent.faith.database.encryption.EncryptedEventInterface.encryptedStreamingDEK]
     */
    val encryptedStreamingDEK: String
)
