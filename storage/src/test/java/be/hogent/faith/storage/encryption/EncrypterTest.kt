package be.hogent.faith.storage.encryption

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDateTime

class EncrypterTest {

    private val encrypter = Encrypter()

    @Test
    fun encrypter_localDateTime_encryptDecryptIdempotent() {
        val localDateTime = LocalDateTime.of(1991, 9, 27, 6, 10, 2)

        assertEquals(
            localDateTime,
            encrypter.decryptLocalDateTime(encrypter.encrypt(localDateTime))
        )
    }

    @Test
    fun encrypter_string_encryptDecryptIdempotent() {
        val testString = "spadif;qasdjavxz;cnv32351-saf-9=as%%"

        assertEquals(
            testString,
            encrypter.decrypt(encrypter.encrypt(testString))
        )
    }
}