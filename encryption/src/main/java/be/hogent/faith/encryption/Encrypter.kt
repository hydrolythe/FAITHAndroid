package be.hogent.faith.encryption

import com.google.crypto.tink.Aead
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.subtle.Base64
import java.io.File


class Encrypter {

    private val EMPTY_ASSOCIATED_DATA = ByteArray(0)
    private val KEY_LOCATION = "/data/user/0/be.hogent.faith/files/"
    private val KEY_NAME = "key.json"

    private lateinit var aead: Aead

    init {
        AeadConfig.register()
        initialiseAead()
    }


    fun encrypt(string: String): String {
        val plainTextBytes = string.toByteArray(charset("UTF-8"))
        val encryptedBytes = aead.encrypt(plainTextBytes, EMPTY_ASSOCIATED_DATA)
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(base64EncryptedString: String): String {
        val encryptedBytes = Base64.decode(base64EncryptedString, Base64.DEFAULT)
        val plainTextBytes = aead.decrypt(encryptedBytes, EMPTY_ASSOCIATED_DATA)
        return plainTextBytes.toString(charset("UTF-8"))
    }

    private fun initialiseAead() {
        if (File(KEY_LOCATION, KEY_NAME).exists()) {
            val keysetHandle = CleartextKeysetHandle.read(
                JsonKeysetReader.withFile(File(KEY_LOCATION, KEY_NAME))
            )
            aead = keysetHandle.getPrimitive(Aead::class.java)
        } else {
            val keySethandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM)
            val saveFile = File(KEY_LOCATION)
            saveFile.mkdirs()
            CleartextKeysetHandle.write(
                keySethandle, JsonKeysetWriter.withFile(File(KEY_LOCATION, KEY_NAME))
            )
            aead = keySethandle.getPrimitive(Aead::class.java)
        }
    }
}
