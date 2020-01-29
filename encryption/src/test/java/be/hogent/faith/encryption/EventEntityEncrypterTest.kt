package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.factory.EventFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventEntityEncrypterTest {
    private val keyGenerator = KeyGenerator()
    private val encrypter = KeyEncrypter(DummyKeyEncryptionService())

    private val evenEncrypter = EventEncryptionService(keyGenerator, encrypter)

    private val event = EventFactory.makeEvent()

    @Test
    fun eventEntityCanBeEncrypted() {
        // Act
        evenEncrypter.encrypt(event)
            .test()
            .assertNoErrors()
            .assertComplete()
    }

    @Test
    fun encryptedEventEntityCanBeDecrypted() {
        // Arrange
        var encryptedEvent: EncryptedEvent? = null
        evenEncrypter.encrypt(event)
            .doOnSuccess { encryptedEvent = it }
            .test()

        // Act
        var decryptedEvent: Event? = null
        evenEncrypter.decrypt(encryptedEvent!!)
            .doOnSuccess { decryptedEvent = it }
            .test()

        // Assert
        assertEquals(decryptedEvent, event)
    }

    @Test
    fun encryptedEventEntityHasADataEncryptionKeys() {
        // Act
        // Arrange
        var encryptedEvent: EncryptedEvent? = null
        evenEncrypter.encrypt(event)
            .doOnSuccess { encryptedEvent = it }
            .test()

        // Assert
        assertTrue(encryptedEvent!!.encryptedDEK.isNotEmpty())
        assertTrue(encryptedEvent!!.encryptedStreamingDEK.isNotEmpty())
    }
}