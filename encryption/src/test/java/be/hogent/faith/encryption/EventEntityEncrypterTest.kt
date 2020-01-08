package be.hogent.faith.encryption

import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.factory.DataFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventEntityEncrypterTest {
    private val keyGenerator = KeyGenerator()
    private val encrypter = KeyEncrypter(
        DummyKeyEncryptionService()
    )
    private val eventEntityEncryptor =
        EventEntityEncrypter(keyGenerator, encrypter)

    private val eventEntity = EventEntity(
        LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
        "test",
        "testpath",
        "string",
        "uuuid",
        emptyList()
    )

    @Test
    fun eventEntityCanBeEncrypted() {
        // Act
        eventEntityEncryptor.encrypt(eventEntity)
    }

    @Test
    fun encryptedEventEntityCanBeDecrypted() {
        // Arrange
        val encryptedEntity = eventEntityEncryptor.encrypt(eventEntity)

        // Act
        val decryptedEntity = eventEntityEncryptor.decrypt(encryptedEntity)

        // Assert
        assertEquals(eventEntity, decryptedEntity)
    }

    @Test
    fun encryptedEventEntityHasADataEncryptionKey() {
        // Act
        val encryptedEntity = eventEntityEncryptor.encrypt(eventEntity)

        // Assert
        assertTrue(encryptedEntity.encryptedDEK.isNotEmpty())
    }
}