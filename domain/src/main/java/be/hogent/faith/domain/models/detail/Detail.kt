package be.hogent.faith.domain.models.detail

import java.io.File
import java.io.Serializable
import java.util.UUID

/**
 * A detail that can be part of an event, backpack, solution,...
 */

sealed class Detail(
    var file: File,
    var fileName: String = "",
    val uuid: UUID = UUID.randomUUID()
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Detail

        if (file != other.file) return false
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + uuid.hashCode()
        return result
    }
}

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
