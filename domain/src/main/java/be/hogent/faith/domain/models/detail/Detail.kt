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
    var fileName: String = "",
    val uuid: UUID = UUID.randomUUID()
) : Serializable

class DrawingDetail(
    file: File,
    fileName: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, fileName, uuid)

class PhotoDetail(
    file: File,
    fileName: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, fileName, uuid)

class TextDetail(
    file: File,
    fileName: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, fileName, uuid)

class AudioDetail(
    file: File,
    fileName: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, fileName, uuid)
class VideoDetail(
    file: File,
    fileName: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, fileName, uuid)
class ExternalVideoDetail(
    file: File,
    fileName: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, fileName, uuid)
