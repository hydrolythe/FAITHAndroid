package be.hogent.faith.encryption

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import com.google.crypto.tink.Aead
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailEntityEncrypterTest {
    private val dataEncryptionKey by lazy { createDEK() }

    private fun createDEK(): Aead {
        return KeyGenerator().generateKeysetHandle().getPrimitive(Aead::class.java)
    }

    private val detailEntityEncrypter = DetailEntityEncrypter(DataEncrypter(dataEncryptionKey))

    private val detailEntity = DetailEntity(
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