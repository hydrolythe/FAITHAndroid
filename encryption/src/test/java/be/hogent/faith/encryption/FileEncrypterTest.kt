package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileEncrypterTest {
    private val dataEncryptionKey = KeyGenerator().generateStreamingKeysetHandle()

    private val fileEncrypter = FileEncryptionService(dataEncryptionKey)

    private val workingFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image.png")
    private val originalFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image - copy.png")

    @After
    fun tearDown() {
        originalFile.copyTo(workingFile, overwrite = true)
    }

    @Test
    fun fileDoesNotHaveSameContentsAfterEncryption() {
        // Act
        fileEncrypter.encrypt(workingFile)
            .test()
            .assertComplete()
            .dispose()

        // Assert
        assertFalse(originalFile.contentEqual(workingFile))
    }

    @Test
    fun encryptedFileIsDecryptedCorrectly() {
        // Act
        fileEncrypter.encrypt(workingFile)
            .andThen(Completable.defer {
                fileEncrypter.decrypt(workingFile)
            })
            .test()
            .assertComplete()
            .dispose()

        // Assert
        assertTrue(originalFile.contentEqual(workingFile))
    }
}