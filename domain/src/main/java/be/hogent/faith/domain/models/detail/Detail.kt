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
