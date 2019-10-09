package be.hogent.faith.database.models

import java.util.UUID

data class EventEntity(
    val dateTime: String = "",
    val title: String = "",
    val emotionAvatar: String? = null,
    val notes: String? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val details: List<DetailEntity> = emptyList()
)