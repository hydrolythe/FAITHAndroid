package be.hogent.faith.database.encryption

import java.io.File
import java.util.UUID

class EncryptedEvent(
    val dateTime: EncryptedString,
    val title: EncryptedString,
    /**
     * The [emotionAvatar] of an event is nonsensitive information so it can stay unencrypted
     */
    val emotionAvatar: File?,
    val notes: EncryptedString?,
    /**
     * The [uuid] of an event is nonsensitive information so it can stay unencrypted
     */
    val uuid: UUID,
    val details: List<EncryptedDetail>,
    /**
     * An encrypted version of the Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting small pieces of data directly.
     */
    val encryptedDEK: EncryptedString,
    /**
     * An encrypted version of the streaming Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting entire files as a stream.
     */
    val encryptedStreamingDEK: EncryptedString
)
