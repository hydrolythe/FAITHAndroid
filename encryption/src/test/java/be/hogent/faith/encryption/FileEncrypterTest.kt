package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import be.hogent.faith.util.withSuffix
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Completable
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

    private val originalFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image.png")
    private val workingFile = File(originalFile.path.withSuffix("working"))

    @Before
    fun backUpFileToBeEncrypted() {
        originalFile.copyTo(
            target = workingFile,
            overwrite = true
        )
    }

    @After
    fun restoreFileToBeEncrypted() {
        workingFile.delete()
    }

    @Test
    fun fileDoesNotHaveSameContentsAfterEncryption() {
        // Act
        fileEncrypter.encrypt(workingFile)
            .test()
            .assertComplete()

        // Assert
        assertFalse(workingFile.contentEqual(originalFile))
    }

    @Test
    fun encryptedFileIsDecryptedCorrectly() {
        // Act
        fileEncrypter.encrypt(workingFile)
            .andThen(
                Completable.defer { fileEncrypter.decrypt(workingFile) }
            )
            .test()
            .assertComplete()

        // Assert
//        assertEquals(originalFile.readText(), file.readText())
        assertTrue(workingFile.contentEqual(originalFile))
    }
}