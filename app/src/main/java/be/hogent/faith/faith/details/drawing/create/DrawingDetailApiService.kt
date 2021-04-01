package be.hogent.faith.faith.details.drawing.create

import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import be.hogent.faith.faith.models.retrofitmodels.OverwritableImageDetail
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import java.io.File

interface DrawingDetailApiService {
    @Multipart
    @PUT("/draw")
    fun getDrawingDetail(@Part part:MultipartBody.Part): Call<DrawingDetail>
    @PUT("/draw/over")
    fun overwriteDetail(@Body overwritableDetail:OverwritableImageDetail): Call<DrawingDetail>
}