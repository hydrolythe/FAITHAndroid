package be.hogent.faith.encryption.encryptionService

import org.junit.Test

class KMSApiTest {
    private val api = KMSApi()
    @Test
    fun KMSApi_encrypt() {
        val input = "testtString"
        val plainText = input.toByteArray(charset("UTF-8"))
        api.encrypt(plainText)
    }
}