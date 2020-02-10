package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
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

    private val file =
        File("src/test/java/be/hogent/faith/encryption/testResources/image.png")
    private val backupFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image - copy.png")


    @Before
    fun setUp() {
        file.delete()
        backupFile.copyTo(file, overwrite = true)
    }

    @After
    fun tearDown() {
        file.delete()
        backupFile.copyTo(file, overwrite = true)
    }

    @Test
    fun fileDoesNotHaveSameContentsAfterEncryption() {
        // Act
        fileEncrypter.encrypt(file)
            .test()
            .assertComplete()
            .dispose()

        // Assert
        assertFalse(backupFile.contentEqual(file))
    }

    @Test
    fun encryptedFileIsDecryptedCorrectly() {
        // Act
        fileEncrypter.encrypt(file)
            .test()
            .assertComplete()
            .dispose()

        fileEncrypter.decrypt(file)
            .test()
            .assertComplete()
            .dispose()

        // Assert
        val result =backupFile.contentEqual(file)
        assertTrue(result)
    }
}