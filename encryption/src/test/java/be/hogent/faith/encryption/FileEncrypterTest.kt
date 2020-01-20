package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import com.google.crypto.tink.KeysetHandle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileEncrypterTest {
    private val dataEncryptionKey by lazy { createDEK() }

    private fun createDEK(): KeysetHandle {
        return KeyGenerator().generateStreamingKeysetHandle()
    }

    private val fileEncrypter = FileEncrypter(dataEncryptionKey)

    private val file = File("src/test/java/be/hogent/faith/encryption/testResources/screenshot.png")


    @Test
    fun fileDoesNotHaveSameContentsAfterEncryption() {
        // Act
        val encryptedFile = fileEncrypter.encrypt(file)

        // Assert
        assertFalse(file.contentEqual(encryptedFile))
    }

    @Test
    fun encryptedFileIsDecryptedCorrectly() {
        // Arrange
        val resultingFile = fileEncrypter.encrypt(file)

        // Act
        val decryptedFile = fileEncrypter.decrypt(resultingFile)

        // Assert
        assertTrue(file.contentEqual(decryptedFile))

    }
}