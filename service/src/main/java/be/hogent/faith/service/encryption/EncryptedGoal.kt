package be.hogent.faith.service.encryption

import java.util.UUID

class EncryptedGoal(
    val uuid: UUID,
    var description: EncryptedString,
    var dateTime: EncryptedString,
    var isCompleted: Boolean = false,
    /**
     * An encrypted version of the Data Encryption Key, used for envelope encryption.
     * This DEK is used for encrypting small pieces of data directly.
     */
    val encryptedDEK: EncryptedString
)