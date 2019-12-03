package be.hogent.faith.encryption

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class EncrypterTest {

    private val encrypter = Encrypter()

    @Test
    fun encrypter_string_encryptDecryptIdempotent() {
        val testString = "spadif;qasdjavxz;cnv32351-saf-9=as%%"

        val encryptedString = encrypter.encrypt(testString)

        assertNotEquals(testString, encryptedString)

        assertEquals(
            testString,
            encrypter.decrypt(encryptedString)
        )
    }
}