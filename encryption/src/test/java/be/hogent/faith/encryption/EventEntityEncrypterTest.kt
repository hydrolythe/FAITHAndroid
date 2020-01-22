package be.hogent.faith.encryption

import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventEntityEncrypterTest {
    private val keyGenerator = KeyGenerator()
    private val encrypter = KeyEncrypter(
        DummyKeyEncryptionService()
    )
    private val eventEntityEncryptor =
        EventEncryptionServiceInterface(keyGenerator, encrypter)

    private val event = EventFactory.makeEvent()
    private val detailEntity = EncryptedDetailEntity(
        file = "src/test/java/be/hogent/faith/encryption/testResources/screenshot.png",
        uuid = "uuuid",
        type = DetailType.TEXT

    )
    private val eventEntity = EncryptedEventEntity(
        LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
        "test",
        "testpath",
        "string",
        "uuuid",
        listOf(detailEntity)
    )

    @Test
    fun eventEntityCanBeEncrypted() {
        // Act
        eventEntityEncryptor.encrypt(event)
    }

    @Test
    fun encryptedEventEntityCanBeDecrypted() {
        // Arrange
        val encryptedEntity = eventEntityEncryptor.encrypt(event)

        // Act
        val decryptedEntity = eventEntityEncryptor.decrypt(encryptedEntity)

        // Assert
        assertEquals(eventEntity, decryptedEntity)
    }

    @Test
    fun encryptedEventEntityHasADataEncryptionKeys() {
        // Act
        val encryptedEntity = eventEntityEncryptor.encrypt(event)

        // Assert
        assertTrue(encryptedEntity.encryptedDEK.isNotEmpty())
        assertTrue(encryptedEntity.encryptedStreamingDEK.isNotEmpty())
    }
}