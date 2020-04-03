package be.hogent.faith.storage

import be.hogent.faith.service.usecases.encryption.EncryptedEvent
import be.hogent.faith.storage.local.ILocalFileStorageRepository
import be.hogent.faith.storage.online.IOnlineFileStorageRepository
import be.hogent.faith.storage.local.ITemporaryFileStorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test
import java.io.File
import java.util.UUID

class FileStorageRepositoryTest {

    private val localStorage = mockk<ILocalFileStorageRepository>()
    private val remoteStorage = mockk<IOnlineFileStorageRepository>()
    private val temporaryStorage = mockk<ITemporaryFileStorageRepository>()
    private val storageRepository =
        FileStorageRepository(temporaryStorage, localStorage, remoteStorage)

    private val encryptedEvent = EncryptedEvent(
        dateTime = "encrypted DateTime",
        uuid = UUID.randomUUID(),
        title = "encrypted title",
        notes = "encrypted notes",
        emotionAvatar = File("path/to/emotionAvatar"),
        details = emptyList(),
        encryptedDEK = "encrypted DEK",
        encryptedStreamingDEK = "encrypted SDEK"
    )

    private val event = EventFactory.makeEvent(numberOfDetails = 2, hasEmotionAvatar = true)

    @Test
    fun `saveEvent passes the event to local storage`() {
        // Arrange
        every { localStorage.saveEventFiles(encryptedEvent) } returns Single.just(encryptedEvent)
        every { remoteStorage.saveEventFiles(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEventFiles(encryptedEvent)
            .test()
            .dispose()

        verify(exactly = 1) { localStorage.saveEventFiles(encryptedEvent) }
    }

    @Test
    fun `saveEvent passes the event to remote storage`() {
        // Arrange
        every { localStorage.saveEventFiles(encryptedEvent) } returns Single.just(encryptedEvent)
        every { remoteStorage.saveEventFiles(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEventFiles(encryptedEvent)
            .test()
            .dispose()

        verify(exactly = 1) { remoteStorage.saveEventFiles(encryptedEvent) }
    }

    @Test
    fun `saveEvent does not store in remote storage when saving to local storage fails`() {
        // Arrange
        every { localStorage.saveEventFiles(any()) } returns Single.error(RuntimeException())
        every { remoteStorage.saveEventFiles(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEventFiles(encryptedEvent)
            .test()
            .dispose()

        verify { remoteStorage.saveEventFiles(any()) wasNot Called }
    }

    @Test
    fun `saveEvent emits an error when saving to local storage fails`() {
        // Arrange
        every { localStorage.saveEventFiles(any()) } returns Single.error(RuntimeException())
        every { remoteStorage.saveEventFiles(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
            .dispose()
    }

    @Test
    fun `saveEvent emits an error when saving to remote storage fails`() {
        // Arrange
        every { localStorage.saveEventFiles(any()) } returns Single.just(encryptedEvent)
        every { remoteStorage.saveEventFiles(any()) } returns Completable.error(RuntimeException())

        // Act
        storageRepository.saveEventFiles(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
            .dispose()
    }

    @Test
    fun `downloadEventFiles does not call remoteStorage when all files are locally available`() {
        // Arrange
        event.details.forEach {
            every { localStorage.isFilePresent(it, event) } returns true
        }
        every { localStorage.isEmotionAvatarPresent(event) } returns true

        // Act
        storageRepository.downloadEventFiles(event)
            .test()
            .dispose()

        // Assert
        event.details.forEach {
            verify { remoteStorage.downloadDetail(it, event) wasNot Called }
        }
        verify { remoteStorage.downloadEmotionAvatar(event) wasNot Called }
    }

    @Test
    fun `downloadEventFiles calls remoteStorage to download the details when they aren't locally available `() {
        // Arrange
        every { localStorage.isFilePresent(event.details[0], event) } returns false
        every { localStorage.isFilePresent(event.details[1], event) } returns false

        every { localStorage.isEmotionAvatarPresent(event) } returns true

        every {
            remoteStorage.downloadDetail(
                event.details[0],
                event
            )
        } returns Completable.complete()
        every {
            remoteStorage.downloadDetail(
                event.details[1],
                event
            )
        } returns Completable.complete()

        // Act
        storageRepository.downloadEventFiles(event)
            .test()
            .dispose()

        // Assert
        verifyAll {
            remoteStorage.downloadDetail(event.details[0], event)
            remoteStorage.downloadDetail(event.details[1], event)
        }
        verify { remoteStorage.downloadEmotionAvatar(event) wasNot Called }
    }

    @Test
    fun `downloadEventFiles calls remoteStorage to download the emotionAvatar when it is not locally available `() {
        // Arrange
        every { localStorage.isFilePresent(event.details[0], event) } returns true
        every { localStorage.isFilePresent(event.details[1], event) } returns true

        every { localStorage.isEmotionAvatarPresent(event) } returns false
        every { remoteStorage.downloadEmotionAvatar(event) } returns Completable.complete()

        // Act
        storageRepository.downloadEventFiles(event)
            .test()
            .dispose()

        // Assert
        verifyAll {
            remoteStorage.downloadDetail(event.details[0], event) wasNot Called
            remoteStorage.downloadDetail(event.details[1], event) wasNot Called
        }
        verify { remoteStorage.downloadEmotionAvatar(event) }
    }

    @Test
    fun `downloadEventFiles does not contact local or remote storage when the event has no EmotionAvatar `() {
        // Arrange
        val event = EventFactory.makeEvent(numberOfDetails = 0, hasEmotionAvatar = false)

        // Act
        storageRepository.downloadEventFiles(event)
            .test()
            .dispose()

        // Assert
        verify { remoteStorage.downloadEmotionAvatar(event) wasNot Called }
    }
}
