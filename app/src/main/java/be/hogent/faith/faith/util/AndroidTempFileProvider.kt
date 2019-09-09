package be.hogent.faith.faith.util

import android.content.Context
import java.io.File

class AndroidTempFileProvider(private val context: Context) : TempFileProvider {
    override val tempPhotoFile: File
        get() = File(context.cacheDir, "tempPhoto.PNG")
    override val tempAudioRecordingFile: File
        get() = File(context.cacheDir, "tempRecording.3gp")
}