package be.hogent.faith.domain.models.detail

import java.io.File
import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */
abstract class Detail(
    /**
     * The location of this Detail
     */
    val file: File,
    val name: String? = null,
    val uuid: UUID = UUID.randomUUID()
)
