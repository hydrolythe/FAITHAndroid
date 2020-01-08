package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import com.google.crypto.tink.Aead
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class FileEncrypterTest {
    private val dataEncryptionKey by lazy { createDEK() }

    private fun createDEK(): Aead {
        return KeyGenerator().generateKeysetHandle().getPrimitive(Aead::class.java)
    }
    private val fileEncrypter = FileEncrypter(dataEncryptionKey)

    private val file = File("location")
    private val fileContents = "File contents"

    @Before
    fun setUp() {
        file.writeText(fileContents)
    }

    @Test
    fun fileDoesNotHaveSameContentsAfterEncryption() {
        // Act
        fileEncrypter.encrypt(file)

        // Assert
        assertNotEquals(fileContents, file.readText())
    }

    @Test
    fun encryptedFileIsDecryptedCorrectly() {
        // Arrange
        fileEncrypter.encrypt(file)

        // Act
        fileEncrypter.decrypt(file)

        // Assert
        assertEquals(fileContents, file.readText())
    }

    @After
    fun removeFile() {
        file.delete()
    }
}