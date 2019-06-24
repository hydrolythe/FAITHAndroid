package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import org.jetbrains.annotations.TestOnly
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

data class Event(
    /**
     * The time when this event occured.
     */
    var dateTime: LocalDateTime = LocalDateTime.now(),

    /**
     * The title doesn't have to be filled in immediately when recording it, but it _has_ to be filled in once the
     * mentor fills in the overview.
     */
    var title: String? = null,
    /**
     * The file where the colored avatar is saved.
     * It is first saved when the user draws something, before that the property is null.
     */
    var emotionAvatar: File? = null,

    /**
     * The notes that were added to this event.
     */
    var notes: String? = null,

    val uuid: UUID = UUID.randomUUID()
) {

    private val _details = mutableListOf<Detail>()
    val details: List<Detail>
        get() = _details

    fun getLastDetail(): Detail {
        return _details.last()
    }

    @TestOnly
    fun addDetail(detail: Detail) {
        _details += detail
    }

    fun addNewPictureDetail(saveFile: File, photoName: String) {
        addDetail(PictureDetail(saveFile, photoName))
    }

    fun addNewAudioDetail(saveFile: File, audioRecordingName: String) {
        addDetail(AudioDetail(saveFile, audioRecordingName))
    }

    fun addNewTextDetail(saveFile: File, textDetailName: String) {
        addDetail(TextDetail(saveFile, textDetailName))
    }
}