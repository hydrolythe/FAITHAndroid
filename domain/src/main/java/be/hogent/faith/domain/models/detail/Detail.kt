package be.hogent.faith.domain.models.detail

import java.io.File
import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */
sealed class Detail(
    /**
     * The location of this Detail
     */
    val file: File,
    // TODO: remove or not? Keep filled in by default now to avoid errors
    val name: String? = "Detail",
    val uuid: UUID = UUID.randomUUID()
)

class DrawingDetail(
    file: File,
    name: String? = null,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, name, uuid)

class PhotoDetail(
    file: File,
    name: String? = null,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, name, uuid)

class TextDetail(
    file: File,
    name: String? = null,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, name, uuid)

class AudioDetail(
    file: File,
    name: String? = null,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, name, uuid)
