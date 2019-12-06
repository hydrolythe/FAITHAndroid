package be.hogent.faith.database.models

import java.util.UUID

data class DetailEntity(
    var file: String = "",
    val uuid: String = UUID.randomUUID().toString(),
    val type: DetailType? = null
)

enum class DetailType {
    TEXT, AUDIO, DRAWING, PHOTO
}