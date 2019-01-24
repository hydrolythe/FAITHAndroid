package be.hogent.faith.domain.models

import java.util.UUID

data class Detail(
    val detailType: DetailType,
    val uuid: UUID = UUID.randomUUID()
)
