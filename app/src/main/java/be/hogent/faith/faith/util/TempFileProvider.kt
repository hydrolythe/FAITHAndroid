package be.hogent.faith.faith.util

import java.io.File

interface TempFileProvider {
    val tempPhotoFile: File
    val tempAudioRecordingFile: File
}