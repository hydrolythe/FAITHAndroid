package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileEncrypterTest : TestWithFiles() {
    private val dek = KeyGenerator().generateStreamingKeysetHandle()

    private val fileEncrypter = FileEncryptionService()

    private val plainTextFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image.png")
    private val originalFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image - copy.png")

    @After
    fun tearDown() {
        originalFile.copyTo(plainTextFile, overwrite = true)
    }


    @Test
    fun `the original file remains untouched after encryption`() {
        // Act
        fileEncrypter.encrypt(plainTextFile, dek)
            .test()
            .assertComplete()
            .dispose()

        // Assert
        assertTrue(originalFile.contentEqual(plainTextFile))
    }

    @Test
    fun `The encrypted file is different from the original file`() {
        // Arrange
        lateinit var encryptedFile: File

        // Act
        fileEncrypter.encrypt(plainTextFile, dek)
            .doOnSuccess { encryptedFile = it }
            .test()
            .assertComplete()
            .dispose()

        // Assert
        assertFalse(encryptedFile.contentEqual(plainTextFile))
    }

    @Test
    fun `decrypting an encrypted file returns a file with the original contents`() {
        // Arrange
        lateinit var encryptedFile: File
        lateinit var decryptedFile: File
        fileEncrypter.encrypt(plainTextFile, dek)
            .doOnSuccess { encryptedFile = it }
            .test()
            .assertComplete()
            .dispose()

        // Act
        fileEncrypter.decrypt(encryptedFile, dek)
            .doOnSuccess { decryptedFile = it }
            .test()
            .assertComplete()
            .dispose()

        // Assert
        assertTrue(decryptedFile.contentEqual(originalFile))
    }
}