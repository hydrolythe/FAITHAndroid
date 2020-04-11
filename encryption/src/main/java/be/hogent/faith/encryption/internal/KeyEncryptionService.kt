package be.hogent.faith.encryption.internal

import com.squareup.moshi.JsonClass
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

const val ENDPOINT = "https://encryptionapi-bvjyzvhieq-ew.a.run.app"

interface KeyEncryptionService {

    @POST("encrypt")
    fun encrypt(@Body request: EncryptionRequest): Single<String>

    @POST("decrypt")
    fun decrypt(@Body request: DecryptionRequest): Single<String>
}

@JsonClass(generateAdapter = true)
data class EncryptionRequest(val plaintext: String)

@JsonClass(generateAdapter = true)
data class DecryptionRequest(val ciphertext: String)
