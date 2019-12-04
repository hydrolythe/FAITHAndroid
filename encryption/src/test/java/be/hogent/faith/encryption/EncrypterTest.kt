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

    @Test
    fun encrypter_filePath_decryptsOK() {
        val path =
            "/data/user/0/be.hogent.faith/cache/users/9eax9EFj1VcifHHWrIVFIWnrthm1/events/0400c72c-667b-412a-b7be-76ac9a0a52e7/902b9e4b-e785-4b1c-bcc6-380d0cba8367"
        val encryptedPath = encrypter.encrypt(path)

        assertNotEquals(path, encryptedPath)

        assertEquals(
            path,
            encrypter.decrypt(encryptedPath)
        )
    }

    @Test
    fun encrypter_decryptFireBasePath() {
        val encryptedPath = "AUTUyMDdP4MgsYjonxHybDddTlSF22HDc0MX4gsQU+hXoJRV04gzFD8BCCA7m3o97ic7eIoKImVMtop4/AujjVczXl0lry/sIf6pA0sQWHeRVDPCa0dOOGiBVhYcr1rDZIRvpD8o0B+T0KdvWMv9eb/6Vg3lxd2oWuNfj/JaEIqg8ojEukT9dgzgcnGlCGrCEOx0wmo2lxqbiQZv/BPn1Yg66qdXqB6e5FbcMMzZLPPsMJ0cJxfP"

        encrypter.decrypt(encryptedPath)
    }
}