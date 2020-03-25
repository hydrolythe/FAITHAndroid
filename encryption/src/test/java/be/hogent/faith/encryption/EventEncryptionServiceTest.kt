package be.hogent.faith.encryption

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import be.hogent.faith.encryption.di.encryptionModule
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.util.contentEqual
import be.hogent.faith.util.factory.EventFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import java.io.File

class EventEncryptionServiceTest : KoinTest {
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
    private val backupFile =
        File("src/test/java/be/hogent/faith/encryption/testResources/image - copy.png")

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(encryptionModule)
    }

    @After
    fun cleanUpFiles() {
        eventWithFiles.emotionAvatar?.delete()
        eventWithFiles.details.forEach {
            it.file.delete()
        }
    }

    @Test
    fun `After encrypting an event none of the sensitive data is in a human readable format`() {
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
            .dispose()
    }

    @Test
    fun `After decrypting an encrypted event all its data is back to the original values`() {
        // Arrange
        var encryptedEvent: EncryptedEvent? = null
        eventEncrypter.encrypt(eventWithoutFiles)
            .doOnSuccess { encryptedEvent = it }
            .test()
            .dispose()

        // Act
        var decryptedEvent: Event? = null
        eventEncrypter.decryptData(encryptedEvent!!)
            .doOnSuccess { decryptedEvent = it }
            .test()
            .assertNoErrors()
            .dispose()

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
                encryptedEvent.emotionAvatar!!.contentEqual(backupFile)
            }
            .dispose()
    }

    @Test
    fun `The details of the event are not the same after encrypting`() {
        // Arrange
        createFilesForEvent(eventWithFiles)

        // Act
        lateinit var encryptedEvent: EncryptedEvent
        eventEncrypter.encrypt(eventWithFiles)
            .doOnSuccess { encryptedEvent = it }
            .test()
            .assertNoErrors()
            .assertComplete()
            .dispose()

        assertTrue(encryptedEvent.details.none { encryptedDetail ->
            encryptedDetail.file.contentEqual(backupFile)
        })
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
            .dispose()

        // Assert
        assertTrue(encryptedEvent!!.encryptedDEK.isNotEmpty())
        assertTrue(encryptedEvent!!.encryptedStreamingDEK.isNotEmpty())
    }
}