package be.hogent.faith.encryption.encryptionService

import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DummyKeyEncryptionService : KeyEncryptionService {
    override fun encrypt(request: EncryptionRequest): Call<String> {
        return object : Call<String> {
            override fun enqueue(callback: Callback<String>) {
            }

            override fun isExecuted(): Boolean {
                return false
            }

            override fun clone(): Call<String> {
                return this
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun cancel() {
            }

            override fun execute(): Response<String> {
                return Response.success("${request.plaintext}xxx")
            }

            override fun request(): Request {
                return Request.Builder().build()
            }
        }
    }

    override fun decrypt(request: DecryptionRequest): Call<String> {
        return object : Call<String> {
            override fun enqueue(callback: Callback<String>) {
            }

            override fun isExecuted(): Boolean {
                return false
            }

            override fun clone(): Call<String> {
                return this
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun cancel() {
            }

            override fun execute(): Response<String> {
                return Response.success(request.ciphertext.removeSuffix("xxx"))
            }

            override fun request(): Request {
                return Request.Builder().build()
            }
        }
    }
}