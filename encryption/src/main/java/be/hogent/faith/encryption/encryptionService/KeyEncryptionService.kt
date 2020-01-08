package be.hogent.faith.encryption.encryptionService

import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

const val ENDPOINT = "https://encryptionapi-bvjyzvhieq-ew.a.run.app"

interface KeyEncryptionService {

    @POST("encrypt")
    fun encrypt(@Body request: EncryptionRequest): Call<String>

    @POST("decrypt")
    fun decrypt(@Body request: DecryptionRequest): Call<String>
}

@JsonClass(generateAdapter = true)
data class EncryptionRequest(val plaintext: String)

@JsonClass(generateAdapter = true)
data class DecryptionRequest(val ciphertext: String)
