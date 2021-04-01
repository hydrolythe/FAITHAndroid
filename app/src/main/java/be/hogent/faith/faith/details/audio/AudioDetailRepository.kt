package be.hogent.faith.faith.details.audio

import android.provider.MediaStore
import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.RetrofitRequestAdapter
import be.hogent.faith.faith.details.audio.AudioDetailResult
import be.hogent.faith.faith.details.audio.IAudioDetailRepository
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class AudioDetailRepository:IAudioDetailRepository {
    override suspend fun getAudioDetail(file:File): AudioDetailResult {
        return withContext(Dispatchers.IO){
            val part = MultipartBody.Part.createFormData(
                "file", file.name, RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    file.readBytes()
                )
            )
            val request = RetrofitAdapter.audioDetailApiService.getAudioDetail(part)
            try {
                val result = request.execute()
                AudioDetailResult(success=result.body())
            }
            catch (e:Exception){
                AudioDetailResult(exception=e)
            }
        }
    }
}