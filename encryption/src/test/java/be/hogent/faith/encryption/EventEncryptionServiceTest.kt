package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import be.hogent.faith.util.factory.EventFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class EventEncryptionServiceTest {
    private val keyGenerator = KeyGenerator()
    private val keyEncrypter = KeyEncrypter(DummyKeyEncryptionService())

    private val eventEncrypter = EventEncryptionService(keyGenerator, keyEncrypter)

    private val eventWithoutFiles =
        EventFactory.makeEvent(numberOfDetails = 0, hasEmotionAvatar = false)

    private val eventWithFiles =
        EventFactory.makeEvent(numberOfDetails = 5, hasEmotionAvatar = true)

    /**
     * Used for the emotionAvatar and details
     */
    private val dataFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image.png")

    @After
    fun cleanUpFiles() {
        eventWithFiles.emotionAvatar?.delete()
        eventWithFiles.details.forEach {
            it.file.delete()
        }
    }

    @Test
    fun `after encrypting none of the sensitive data is in a human readable format`() {
        // Act
        eventEncrypter.encrypt(eventWithoutFiles)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { encryptedEvent ->
                encryptedEvent.dateTime != eventWithoutFiles.dateTime.toString() &&
                        encryptedEvent.notes != eventWithoutFiles.notes &&
                        encryptedEvent.title != eventWithoutFiles.title
            }
    }

    @Test
    fun `event without files is back to original after decrypting`() {
        // Arrange
        var encryptedEvent: EncryptedEvent? = null
        eventEncrypter.encrypt(eventWithoutFiles)
            .doOnSuccess { encryptedEvent = it }
            .test()

        // Act
        var decryptedEvent: Event? = null
        eventEncrypter.decrypt(encryptedEvent!!)
            .doOnSuccess { decryptedEvent = it }
            .test()

        // Assert
        assertEquals(decryptedEvent, eventWithoutFiles)
    }

    @Test
    fun `The emotion avatar of the event is not the same after encrypting`() {
        // Arrange
        createFilesForEvent(eventWithFiles)

        // Act
        eventEncrypter.encrypt(eventWithFiles)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { encryptedEvent ->
                encryptedEvent.emotionAvatar!!.contentEqual(eventWithFiles.emotionAvatar!!)
            }
    }

    @Test
    fun `The details of the event are not the same after encrypting`() {
        // Arrange
        createFilesForEvent(eventWithFiles)

        // Arrange
        var encryptedEvent: EncryptedEvent? = null
        eventEncrypter.encrypt(eventWithoutFiles)
            .doOnSuccess { encryptedEvent = it }
            .test()

        // Act
        var decryptedEvent: Event? = null
        eventEncrypter.decrypt(encryptedEvent!!)
            .doOnSuccess { decryptedEvent = it }
            .test()

        // Assert
        assertEquals(decryptedEvent, eventWithoutFiles)
    }

    private fun createFilesForEvent(eventWithFiles: Event) {
        dataFile.copyTo(eventWithFiles.emotionAvatar!!)
        eventWithFiles.details.forEach {
            dataFile.copyTo(it.file)
        }
    }

    @Test
    fun `event has a DEK and Streaming DEK associated with it after encrypting`() {
        // Act
        // Arrange
        var encryptedEvent: EncryptedEvent? = null
        eventEncrypter.encrypt(eventWithoutFiles)
            .doOnSuccess { encryptedEvent = it }
            .test()

        // Assert
        assertTrue(encryptedEvent!!.encryptedDEK.isNotEmpty())
        assertTrue(encryptedEvent!!.encryptedStreamingDEK.isNotEmpty())
    }
}