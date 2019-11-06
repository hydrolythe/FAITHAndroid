package be.hogent.faith.domain.models.detail

import java.io.File
import java.io.Serializable
import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */

sealed class Detail(
    /**
     * The location of this Detail
     */
    var file: File,
    val uuid: UUID = UUID.randomUUID()
) : Serializable

class DrawingDetail(
    file: File,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, uuid)

class PhotoDetail(
    file: File,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, uuid)

class TextDetail(
    file: File,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, uuid)

class AudioDetail(
    file: File,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, uuid)
