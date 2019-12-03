package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.util.TempFileProvider
import java.io.File

class TestFileProvider : TempFileProvider {
    override fun getFile(detail: Detail): File {
        return File("detailFile")
    }

    override fun getEmotionAvatar(event: Event): File? {
        return File("event/avatar")
    }

    override val tempPhotoFile: File
        get() = File("/tmp/testPhoto")
    override val tempAudioRecordingFile: File
        get() = File("/tmp/testAudio")
}