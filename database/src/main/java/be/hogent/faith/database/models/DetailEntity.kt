package be.hogent.faith.database.models

import java.util.UUID

/**
 * Temporarily added for backpack compatibility
 */
data class DetailEntity(
    var file: String = "",
    var fileName : String = "",
    val uuid: String = UUID.randomUUID().toString(),
    val type: DetailType? = null
)

enum class DetailType {
    TEXT, AUDIO, DRAWING, PHOTO, VIDEO, EXTERNAL_VIDEO
}

