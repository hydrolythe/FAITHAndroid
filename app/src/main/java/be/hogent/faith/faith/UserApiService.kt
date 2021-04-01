package be.hogent.faith.faith

import be.hogent.faith.faith.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface UserApiService {
    @GET("/welcome")
    fun login(@Header("Authorization") s: String): Call<Token>
    @GET("/user/{uuid}")
    fun getUser(@Path("uuid") uuid:String?):Call<User>
}