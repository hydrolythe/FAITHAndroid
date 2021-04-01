package be.hogent.faith.faith.details.audio

import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import java.io.File

interface AudioDetailApiService {
    @Multipart
    @PUT("/audio")
    fun getAudioDetail(@Part part: MultipartBody.Part): Call<AudioDetail>
}