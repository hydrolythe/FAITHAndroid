package be.hogent.faith.faith.util

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import java.io.File

class AndroidTempFileProvider(private val context: Context) : TempFileProvider {
    override fun getEmotionAvatar(event: Event): File? {
        if (event.emotionAvatar?.path?.startsWith("users") == true)
            return File(context.filesDir, event.emotionAvatar!!.path)
        return event.emotionAvatar
    }

    override fun getFile(detail: Detail): File {
        if (detail.file.path.startsWith("users"))
            return File(context.filesDir, detail.file.path)
        return detail.file
    }

    override val tempPhotoFile: File
        get() = File(context.cacheDir, "tempPhoto.PNG")
    override val tempAudioRecordingFile: File
        get() = File(context.cacheDir, "tempRecording.3gp")
}