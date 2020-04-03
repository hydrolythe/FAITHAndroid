package be.hogent.faith.service.encryption

import java.io.File
import java.util.UUID

class EncryptedDetail(
    /**
     * The [file] of a detail is nonsensitive information so it can stay unencrypted
     * Must be a var because the file changes when the file is moved from temp to local storage.
     */
    var file: File,
    var title: EncryptedString,
    /**
     * The [uuid] of a detail is nonsensitive information so it can stay unencrypted
     */
    val uuid: UUID,
    val type: EncryptedString
)