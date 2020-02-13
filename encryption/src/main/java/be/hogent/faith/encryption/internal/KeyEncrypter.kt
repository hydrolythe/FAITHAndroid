package be.hogent.faith.encryption.internal

import be.hogent.faith.database.encryption.EncryptedString
import be.hogent.faith.encryption.encryptionService.DecryptionRequest
import be.hogent.faith.encryption.encryptionService.EncryptionRequest
import be.hogent.faith.encryption.encryptionService.KeyEncryptionService
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeysetHandle
import com.squareup.moshi.Moshi
import io.reactivex.Single
import java.io.ByteArrayOutputStream

class KeyEncrypter(private val encryptionService: KeyEncryptionService) {

    internal fun encrypt(keysetHandle: KeysetHandle): Single<EncryptedString> {
        return Single.fromCallable {
            val byteArrayOutputStream = ByteArrayOutputStream()
            CleartextKeysetHandle.write(
                keysetHandle, JsonKeysetWriter.withOutputStream(
                    byteArrayOutputStream
                )
            )
            val stringToEncrypt = byteArrayOutputStream.toString()
            stringToEncrypt
        }
            .map(::EncryptionRequest)
            .flatMap(encryptionService::encrypt)
    }

    internal fun decrypt(encryptedKey: EncryptedString): Single<KeysetHandle> {
        return encryptionService
            .decrypt(DecryptionRequest(encryptedKey))
            .map(::recreateKeySetHandle)
    }

    private fun recreateKeySetHandle(jsonKeysetHandle: String): KeysetHandle {
        val keySetInputStream = jsonKeysetHandle.byteInputStream()
        return CleartextKeysetHandle.read(JsonKeysetReader.withInputStream(keySetInputStream))
    }
}
