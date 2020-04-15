package be.hogent.faith.domain.models.detail

import java.io.File
import java.io.Serializable
import java.util.UUID

/**
 * A detail that can be part of an event, backpack,...
 */
sealed class Detail(
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

class ExternalVideoDetail(
    file: File,
    title: String = "",
    uuid: UUID = UUID.randomUUID()
) : Detail(file, title, uuid)

class YoutubeVideoDetail(
    file: File = File("YoutubeVideoDetail/has/no/file"),
    title: String = "",
    uuid: UUID = UUID.randomUUID(),
    val videoId: String
) : Detail(file, title, uuid)
