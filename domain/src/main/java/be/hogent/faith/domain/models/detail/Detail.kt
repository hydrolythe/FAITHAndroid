package be.hogent.faith.domain.models.detail

import java.io.File
import java.io.Serializable
import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */

sealed class Detail(
    /**
     * A relative path of where the actual content of the detail is saved.
     * Relative because when getting it from local storage the necessary directory structure is
     * added before the path.
     */
    var file: File,
    var title: String = "",
    val uuid: UUID = UUID.randomUUID()
) : Serializable

class DrawingDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)

class PhotoDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)

class TextDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)

class AudioDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)
class VideoDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)
class ExternalVideoDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)
