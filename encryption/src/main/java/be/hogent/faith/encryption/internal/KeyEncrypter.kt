package be.hogent.faith.encryption.internal

import be.hogent.faith.encryption.encryptionService.DecryptionRequest
import be.hogent.faith.encryption.encryptionService.EncryptionRequest
import be.hogent.faith.encryption.encryptionService.KeyEncryptionService
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeysetHandle
import java.io.ByteArrayOutputStream

class KeyEncrypter(private val encryptionService: KeyEncryptionService) {

    internal fun encrypt(keysetHandle: KeysetHandle): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        CleartextKeysetHandle.write(
            keysetHandle, JsonKeysetWriter.withOutputStream(
                byteArrayOutputStream
            )
        )
        val stringToEncrypt = byteArrayOutputStream.toString()
        return encryptionService.encrypt(EncryptionRequest(stringToEncrypt)).execute().body()!!
    }

    internal fun decrypt(encryptedString: String): KeysetHandle {
        val jsonKeysetHandle =
            encryptionService.decrypt(DecryptionRequest(encryptedString)).execute().body()!!
        val keySetInputStream = jsonKeysetHandle.byteInputStream()
        return CleartextKeysetHandle.read(JsonKeysetReader.withInputStream(keySetInputStream))
    }
}
