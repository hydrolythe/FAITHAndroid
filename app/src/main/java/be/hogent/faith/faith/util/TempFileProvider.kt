package be.hogent.faith.faith.util

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import java.io.File

interface TempFileProvider {
    val tempPhotoFile: File
    val tempAudioRecordingFile: File
    val tempExternalVideoFile: File
    fun getFile(detail: Detail): File
    fun getEmotionAvatar(event: Event): File?
}