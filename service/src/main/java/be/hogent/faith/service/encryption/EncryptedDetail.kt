package be.hogent.faith.service.encryption

import java.io.File
import java.util.UUID

class EncryptedDetail(
    /**
     * The [file] of a detail is nonsensitive information so it can stay unencrypted
     * Must be a var because the file changes when the file is moved from temp to local storage.
     */
    var file: File,
    val title: EncryptedString,
    /**
     * The [uuid] of a detail is nonsensitive information so it can stay unencrypted
     */
    val uuid: UUID,
    val type: EncryptedString,
    val dateTime: EncryptedString,
    val thumbnail: EncryptedString?,
    /**
     * Only filled in when the Detail is a [YoutubeVideoDetail], empty otherwise
     */
    // Only used when encrypting a YoutubeVideoDetail.
    // A subclass was another, possibly cleaner option, but would complicate things more
    // than necessary in the database layer. When more one-off attributes are added, a class hierarchy
    // should be added.
    val youtubeVideoID: EncryptedString
)