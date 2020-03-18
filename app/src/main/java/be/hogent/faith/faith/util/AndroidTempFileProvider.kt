package be.hogent.faith.faith.util

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import java.io.File

class AndroidTempFileProvider(private val context: Context) : TempFileProvider {
    override fun getEmotionAvatar(event: Event): File? {
        // Because the path is a relative path we should prepend it with the context.filesDir
        if (event.emotionAvatar?.path?.startsWith("users") == true) {
            return File(context.filesDir, event.emotionAvatar!!.path)
        } else {
            return event.emotionAvatar
        }
    }

    override fun getFile(detail: Detail): File {
        // Because the path is a relative path we should prepend it with the context.filesDir
        if (detail.file.path.startsWith("users")) {
            return File(context.filesDir, detail.file.path)
        } else {
            return detail.file
        }
    }

    override val tempPhotoFile: File
        get() = File(context.cacheDir, "tempPhoto.PNG")
    override val tempAudioRecordingFile: File
        get() = File(context.cacheDir, "tempRecording.3gp")
    override val tempExternalVideoFile: File
        get() = File(context.cacheDir, "tempVideo.mp4")
}