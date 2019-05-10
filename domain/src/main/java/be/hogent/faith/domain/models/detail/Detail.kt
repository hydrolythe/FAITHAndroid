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
    //TODO: remove or not? Keep filled in by default now to avoid errors
    val name: String? = "Detail",
    val uuid: UUID = UUID.randomUUID()
)
