package be.hogent.faith.domain.models.detail

import org.threeten.bp.LocalDateTime
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
    val uuid: UUID = UUID.randomUUID(),
    var dateTime: LocalDateTime = LocalDateTime.now()
) : Serializable

class DrawingDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

class PhotoDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

class TextDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

class AudioDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

class ExternalVideoDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

class YoutubeVideoDetail(
    file: File = File("YoutubeVideoDetail/has/no/file"),
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    val videoId: String,
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

class FilmDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)
