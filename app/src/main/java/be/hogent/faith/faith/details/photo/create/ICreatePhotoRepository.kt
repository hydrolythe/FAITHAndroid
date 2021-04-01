package be.hogent.faith.faith.details.photo.create

import okhttp3.RequestBody
import java.io.File

interface ICreatePhotoRepository {
    suspend fun createPhoto(file:File):PhotoResult
}