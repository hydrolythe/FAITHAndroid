package be.hogent.faith.storage.encryption

import org.threeten.bp.LocalDateTime
import se.simbio.encryption.Encryption
import java.io.File

class Encrypter {

    private val key = "megasecurekeydiebestzolangmogelijkis"
    private val salt = "bahzozout"
    // TODO: eigen IV maken
    private val iv = ByteArray(16)

    private val encryption = Encryption.getDefault(key, salt, iv)

    fun encrypt(string: String?): String? {
        return encryption.encrypt(string)
    }

    fun decrypt(string: String?): String? {
        return encryption.decrypt(string)
    }

    fun encrypt(localDateTime: LocalDateTime): String {
        return encrypt(localDateTime.toString())!!
    }

    fun decryptLocalDateTime(localDateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(decrypt(localDateTimeString))
    }
}