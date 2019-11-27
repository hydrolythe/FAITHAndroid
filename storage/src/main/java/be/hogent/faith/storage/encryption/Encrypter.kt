package be.hogent.faith.storage.encryption

import org.threeten.bp.LocalDateTime
import java.io.File

class Encrypter {

    fun encrypt(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

    fun decryptLocalDateTime(localDateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(localDateTimeString)
    }

    fun encrypt(string: String?): String? {
        return string
    }

    fun decryptString(string: String?): String? {
        return string
    }

    fun decryptFile(string: String?): File? {
        if (string == null) {
            return null
        } else {
            return File(string)
        }
    }
}