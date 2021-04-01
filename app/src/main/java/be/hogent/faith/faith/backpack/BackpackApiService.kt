package be.hogent.faith.faith.backpack

import be.hogent.faith.faith.models.Backpack
import be.hogent.faith.faith.models.DetailArray
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Streaming

interface BackpackApiService {
    @GET("backpack/{uuid}")
    fun getDetails(@Path("uuid") uuid:String?): Call<DetailArray>
    @PUT("backpack/detail/{uuid}")
    fun getCurrentDetailFile(@Path("uuid") uuid:String?, @Body detail:Detail):Call<Detail>
    @POST("backpack/{uuid}")
    fun postDetails(@Path("uuid") uuid:String?, @Body expandedDetail: ExpandedDetail): Call<ResponseBody>
    @PUT("backpack/{uuid}")
    fun deleteDetails(@Path("uuid") uuid:String?, @Body detail: Detail): Call<ResponseBody>
    @Streaming
    @PUT("backpack")
    fun getFileOutput(@Body detailFile: DetailFile): Call<ResponseBody>
}