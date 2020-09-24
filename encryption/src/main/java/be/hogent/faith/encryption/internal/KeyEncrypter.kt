package be.hogent.faith.encryption.internal

import be.hogent.faith.service.encryption.EncryptedString
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeysetHandle
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.io.ByteArrayOutputStream

class KeyEncrypter(private val encryptionService: KeyEncryptionService) {

    internal fun encrypt(keysetHandle: KeysetHandle): Single<EncryptedString> {
        return convertKeysetHandleToString(keysetHandle)
            .map(::EncryptionRequest)
            .flatMap(encryptionService::encrypt)
    }

    private fun convertKeysetHandleToString(keysetHandle: KeysetHandle): Single<String> {
        return Single.fromCallable {
            val byteArrayOutputStream = ByteArrayOutputStream()
            CleartextKeysetHandle.write(
                keysetHandle, JsonKeysetWriter.withOutputStream(byteArrayOutputStream)
            )
            val stringToEncrypt = byteArrayOutputStream.toString()
            stringToEncrypt
        }
    }

    internal fun decrypt(encryptedKey: EncryptedString): Single<KeysetHandle> {
        return encryptionService
            .decrypt(DecryptionRequest(encryptedKey))
            .map(::convertStringToKeysetHandle)
            .doOnSuccess { Timber.i("Decrypted key using API") }
            .doOnError {
                Timber.e("Error while decrypting key using API: $")
                Timber.e(it)
            }
    }

    private fun convertStringToKeysetHandle(jsonKeysetHandle: String): KeysetHandle {
        val keySetInputStream = jsonKeysetHandle.byteInputStream()
        return CleartextKeysetHandle.read(JsonKeysetReader.withInputStream(keySetInputStream))
    }
}
