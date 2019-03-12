package be.hogent.faith.domain.models

import java.io.File
import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */
data class Detail(
    val detailType: DetailType,
    /**
     * The location of this Detail
     */
    val file: File,
    val uuid: UUID = UUID.randomUUID()
)
