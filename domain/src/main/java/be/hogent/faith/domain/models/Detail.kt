package be.hogent.faith.domain.models

import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */
data class Detail(
    val detailType: DetailType,
    val uuid: UUID = UUID.randomUUID()
)
