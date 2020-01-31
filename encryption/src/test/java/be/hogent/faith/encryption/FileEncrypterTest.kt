package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import be.hogent.faith.util.withSuffix
import com.google.crypto.tink.KeysetHandle
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class FileEncrypterTest {
    private val dataEncryptionKey by lazy { createDEK() }

    private fun createDEK(): KeysetHandle {
        return KeyGenerator().generateStreamingKeysetHandle()
    }

    private val fileEncrypter = FileEncrypter(dataEncryptionKey)

    private val file = File("src/test/java/be/hogent/faith/encryption/testResources/image.png")
    private val originalFile = File(file.path.withSuffix("original"))

    @Before
    fun backUpFileToBeEncrypted() {
        file.copyTo(
            target = originalFile,
            overwrite = true)
    }

    @After
    fun restoreFileToBeEncrypted() {
        originalFile.copyTo(
            target = file,
            overwrite = true)
        originalFile.delete()
    }

    @Test
    fun fileDoesNotHaveSameContentsAfterEncryption() {
        // Act
        fileEncrypter.encrypt(file)
            .test()
            .assertComplete()

        // Assert
        assertFalse(file.contentEqual(originalFile))
    }

    @Test
    fun encryptedFileIsDecryptedCorrectly() {
        // Act
        fileEncrypter.encrypt(file)
            .andThen(
                fileEncrypter.decrypt(file)
            )
            .test()
            .assertComplete()

        // Assert
        assertTrue(file.contentEqual(originalFile))
    }
}