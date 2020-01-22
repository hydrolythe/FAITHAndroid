package be.hogent.faith.encryption

import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import com.google.crypto.tink.KeysetHandle
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailEntityEncrypterTest {
    private val dataEncryptionKey by lazy { createDEK() }

    private fun createDEK(): KeysetHandle {
        return KeyGenerator().generateKeysetHandle()
    }

    private val detailEntityEncrypter = DetailEncryptionService(DataEncrypter(dataEncryptionKey))

    private val detailEntity = EncryptedDetailEntity(
        file = "/location/of/file",
        uuid = "uuid",
        type = DetailType.TEXT
    )

    @Test
    fun detailEntityCanBeEncrypted() {
        // Act
        detailEntityEncrypter.encrypt(detailEntity)
    }

    @Test
    fun encryptedDetailEntityCanBeDecrypted() {
        // Arrange
        val encryptedEntity = detailEntityEncrypter.encrypt(detailEntity)

        // Act
        val decryptedEntity = detailEntityEncrypter.decrypt(encryptedEntity)

        // Assert
        assertEquals(detailEntity, decryptedEntity)
    }
}