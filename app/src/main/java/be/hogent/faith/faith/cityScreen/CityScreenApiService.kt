package be.hogent.faith.faith.cityScreen

import be.hogent.faith.faith.models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CityScreenApiService {
    @POST("/{uuid}")
    fun logout(@Path("uuid") uuid:String): Call<ResponseBody>
}