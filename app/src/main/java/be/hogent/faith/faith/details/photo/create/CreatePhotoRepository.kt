package be.hogent.faith.faith.details.photo.create

import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.RetrofitRequestAdapter
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CreatePhotoRepository:ICreatePhotoRepository {
    override suspend fun createPhoto(file:File): PhotoResult {
        return withContext(Dispatchers.IO){
            val part = MultipartBody.Part.createFormData(
                "file", file.name, RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    file.readBytes()
                )
            )
            val request = RetrofitAdapter.createPhotoApiService.createPhoto(part)
            try {
                val result = request.execute()
                PhotoResult(success=result.body())
            }
            catch (e:Exception){
                PhotoResult(exception=e)
            }
        }
    }
}