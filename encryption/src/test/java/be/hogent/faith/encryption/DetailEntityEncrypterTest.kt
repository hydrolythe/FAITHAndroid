package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.factory.DetailFactory
import com.google.crypto.tink.KeysetHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailEntityEncrypterTest {
    private val dataEncryptionKey by lazy { createDEK() }

    private fun createDEK(): KeysetHandle {
        return KeyGenerator().generateKeysetHandle()
    }

    private val detailEncrypter = DetailEncryptionService(DataEncrypter(dataEncryptionKey))

    private val detail = DetailFactory.makeRandomDetail()

    @Test
    fun detailEntityCanBeEncrypted() {
        // Act
        detailEncrypter.encrypt(detail)
    }

    @Test
    fun encryptedDetailEntityCanBeDecrypted() {
        // Arrange
        val encryptedDetail = detailEncrypter.encrypt(detail)

        // Act
        val decryptedDetail = detailEncrypter.decrypt(encryptedDetail)

        // Assert
        assertEquals(detail, decryptedDetail)
    }
}