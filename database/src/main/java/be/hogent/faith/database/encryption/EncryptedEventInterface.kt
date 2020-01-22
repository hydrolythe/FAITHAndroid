package be.hogent.faith.database.encryption

import java.io.File
import java.util.UUID

// TODO: check if these interfaces in between the database and encryption layer are necessary
interface EncryptedEventInterface {
    val dateTime: EncryptedString
    val title: EncryptedString
    /**
     * The [emotionAvatar] of an event is nonsensitive information so it can stay unencrypted
     */
    val emotionAvatar: File

    val notes: EncryptedString?
    /**
     * The [uuid] of an event is nonsensitive information so it can stay unencrypted
     */
    val uuid: UUID
    val details: List<EncryptedDetailInterface>

    /**
     * An encrypted version of the Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting small pieces of data directly.
     */
    val encryptedDEK: EncryptedString

    /**
     * An encrypted version of the streaming Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting entire files as a stream.
     */
    val encryptedStreamingDEK: EncryptedString
}

internal class EncryptedEvent(
    override val dateTime: EncryptedString,
    override val title: EncryptedString,
    override val emotionAvatar: File,
    override val notes: EncryptedString?,
    override val uuid: UUID,
    override val details: List<EncryptedDetail>,
    override val encryptedDEK: EncryptedString,
    override val encryptedStreamingDEK: EncryptedString
) : EncryptedEventInterface