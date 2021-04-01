package be.hogent.faith.faith.details.photo.create

import be.hogent.faith.faith.models.detail.PhotoDetail
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

interface CreatePhotoApiService {
    @Multipart
    @PUT("/photo")
    fun createPhoto(@Part part: MultipartBody.Part): Call<PhotoDetail>
}