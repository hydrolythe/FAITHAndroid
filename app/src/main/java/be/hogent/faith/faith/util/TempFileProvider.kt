package be.hogent.faith.faith.util

import android.content.Context
import java.io.File

class TempFileProvider(private val context: Context) {
    val tempPhotoFile: File
        get() = File(context.cacheDir, "tempPhoto.PNG")
    val tempAudioRecordingFile: File
        get() = File(context.cacheDir, "tempRecording.3gp")
}