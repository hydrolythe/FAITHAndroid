package be.hogent.faith.database.models

import java.util.UUID

data class FilmEntity(
    var title: String = "",
    var dateTime: String = "",
    var file: String = "",
    val uuid: String = UUID.randomUUID().toString()
)
