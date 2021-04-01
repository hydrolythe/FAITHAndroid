package be.hogent.faith.faith.loginOrRegister.registerAvatar

import be.hogent.faith.faith.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegisterAvatarApiService {
    @POST("/register")
    fun register(@Body user: User): Call<User>
}