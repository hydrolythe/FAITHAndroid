package be.hogent.faith.database.models

import java.util.UUID

data class DetailsEntity (
    val file: String="",
    val name: String? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val type : Int = 1
){}