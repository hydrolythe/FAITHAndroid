package be.hogent.faith.faith.models.detail

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDateTime
import java.io.File
import java.io.Serializable
import java.util.UUID

/**
 * A detail that can be part of an event, solution,...
 */
@Parcelize
open class Detail constructor(
    /**
     * A relative path of where the actual content of the detail is saved.
     * Relative because when getting it from local storage the necessary directory structure is
     * added before the path.
     */
    open var file: File,
    open var title: String = "",
    open val uuid: UUID = UUID.randomUUID(),
    open var dateTime: LocalDateTime = LocalDateTime.now(),
    // contains a string : base64 encoded thumbnail of the image.
    open var thumbnail: String? = null
) : Serializable,Parcelable

enum class DetailType{
    DRAWING,PHOTO,TEXT,AUDIO,VIDEO,YOUTUBE
}
@Parcelize
class ExpandedDetail(
    override var file: File,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now(),
    override var thumbnail: String?,
    var detailType:DetailType
):Detail(file,title,uuid,dateTime,thumbnail)
@Parcelize
class DrawingDetail(
    override var file: File,
    override var thumbnail: String?,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime, thumbnail)
@Parcelize
class PhotoDetail(
    override var file: File,
    override var thumbnail: String?,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime, thumbnail)
@Parcelize
class TextDetail(
    override var file: File,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)
@Parcelize
class AudioDetail(
    override var file: File,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)
@Parcelize
class VideoDetail(
    override var file: File,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now(),
    override var thumbnail: String? = null
) : Detail(file, title, uuid, dateTime, thumbnail)
@Parcelize
class YoutubeVideoDetail(
    override var file: File = File("YoutubeVideoDetail/has/no/file"),
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    val videoId: String,
    override var dateTime: LocalDateTime = LocalDateTime.now()
) : Detail(file, title, uuid, dateTime)

/**
 * Represenents films that have been made using the Cinema functionality.
 */
@Parcelize
class FilmDetail(
    override var file: File,
    override var title: String = "",
    override val uuid: UUID = UUID.randomUUID(),
    override var dateTime: LocalDateTime = LocalDateTime.now(),
    override var thumbnail: String?
) : Detail(file, title, uuid, dateTime, thumbnail)
