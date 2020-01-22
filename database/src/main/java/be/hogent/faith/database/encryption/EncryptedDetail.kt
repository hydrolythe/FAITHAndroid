package be.hogent.faith.database.encryption

import java.io.File
import java.util.UUID

class EncryptedDetail(
    /**
     * The [file] of a detail is nonsensitive information so it can stay unencrypted
     */
    val file: File,
    /**
     * The [uuid] of a detail is nonsensitive information so it can stay unencrypted
     */
    val uuid: UUID,
    val type: EncryptedString
)