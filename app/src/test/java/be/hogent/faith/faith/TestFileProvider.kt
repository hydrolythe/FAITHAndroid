package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.util.TempFileProvider
import java.io.File

class TestFileProvider : TempFileProvider {
    override fun getFile(detail: Detail): File {
        return File("users/9eax9EFj1VcifHHWrIVFIWnrthm1/events/500d4cfb-aa8c-4e31-99ba-3ff143f0a6ec/f299fd25-4d2b-4a5b-9cf6-c08c954f1d02")
    }

    override fun getEmotionAvatar(event: Event): File? {
        return File("users/9eax9EFj1VcifHHWrIVFIWnrthm1/events/500d4cfb-aa8c-4e31-99ba-3ff143f0a6ec/avatar")
    }

    override val tempPhotoFile: File
        get() = File("/tmp/testPhoto")
    override val tempAudioRecordingFile: File
        get() = File("/tmp/testAudio")
}