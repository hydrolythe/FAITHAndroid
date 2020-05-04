package be.hogent.faith.service.encryption

import java.io.File
import java.util.UUID

class EncryptedEvent(
    val dateTime: EncryptedString,
    val title: EncryptedString,
    /**
     * The [emotionAvatar] of an event is nonsensitive information so it can stay unencrypted
     * It is a var because the file can get moved around (for example while saving or encrypting)
     */
    var emotionAvatar: File?,
    val notes: EncryptedString?,
    /**
     * The [uuid] of an event is nonsensitive information so it can stay unencrypted
     */
    val uuid: UUID,
    // This is var so  we can set this to the list of Encrypted Details once they are encrypted.
    var details: List<EncryptedDetail> = emptyList(),
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
