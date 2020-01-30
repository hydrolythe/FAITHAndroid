package be.hogent.faith.storage

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.storage.ILocalFileStorageRepository
import be.hogent.faith.storage.firebase.IOnlineFileStorageRepository
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
    private val storageRepository = FileStorageRepository(localStorage, remoteStorage)

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
        every { localStorage.saveEvent(encryptedEvent) } returns Single.just(encryptedEvent)
        every { remoteStorage.saveEvent(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEvent(encryptedEvent)
            .test()

        verify(exactly = 1) { localStorage.saveEvent(encryptedEvent) }
    }

    @Test
    fun `saveEvent passes the event to remote storage`() {
        // Arrange
        every { localStorage.saveEvent(encryptedEvent) } returns Single.just(encryptedEvent)
        every { remoteStorage.saveEvent(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEvent(encryptedEvent)
            .test()

        verify(exactly = 1) { remoteStorage.saveEvent(encryptedEvent) }
    }

    @Test
    fun `saveEvent does not store in remote storage when saving to local storage fails`() {
        // Arrange
        every { localStorage.saveEvent(any()) } returns Single.error(RuntimeException())
        every { remoteStorage.saveEvent(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEvent(encryptedEvent)
            .test()

        verify { remoteStorage.saveEvent(any()) wasNot Called }
    }

    @Test
    fun `saveEvent emits an error when saving to local storage fails`() {
        // Arrange
        every { localStorage.saveEvent(any()) } returns Single.error(RuntimeException())
        every { remoteStorage.saveEvent(any()) } returns Completable.complete()

        // Act
        storageRepository.saveEvent(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
    }

    @Test
    fun `saveEvent emits an error when saving to remote storage fails`() {
        // Arrange
        every { localStorage.saveEvent(any()) } returns Single.just(encryptedEvent)
        every { remoteStorage.saveEvent(any()) } returns Completable.error(RuntimeException())

        // Act
        storageRepository.saveEvent(encryptedEvent)
            .test()
            .assertError(RuntimeException::class.java)
    }

    @Test
    fun `downloadEventFiles does not call remoteStorage when all files are locally available`() {
        // Arrange
        event.details.forEach {
            every { localStorage.isFilePresent(it) } returns true
        }
        every { localStorage.isEmotionAvatarPresent(event) } returns true

        // Act
        storageRepository.downloadEventFiles(event)
            .test()

        // Assert
        event.details.forEach {
            verify { remoteStorage.downloadDetail(it) wasNot Called }
        }
        verify { remoteStorage.downloadEmotionAvatar(event) wasNot Called }
    }

    @Test
    fun `downloadEventFiles calls remoteStorage to download the details when they aren't locally available `() {
        // Arrange
        every { localStorage.isFilePresent(event.details[0]) } returns false
        every { localStorage.isFilePresent(event.details[1]) } returns false

        every { localStorage.isEmotionAvatarPresent(event) } returns true

        every { remoteStorage.downloadDetail(event.details[0]) } returns Completable.complete()
        every { remoteStorage.downloadDetail(event.details[1]) } returns Completable.complete()

        // Act
        storageRepository.downloadEventFiles(event)
            .test()

        // Assert
        verifyAll {
            remoteStorage.downloadDetail(event.details[0])
            remoteStorage.downloadDetail(event.details[1])
        }
        verify { remoteStorage.downloadEmotionAvatar(event) wasNot Called }
    }

    @Test
    fun `downloadEventFiles calls remoteStorage to download the emotionAvatar when it is not locally available `() {
        // Arrange
        every { localStorage.isFilePresent(event.details[0]) } returns true
        every { localStorage.isFilePresent(event.details[1]) } returns true

        every { localStorage.isEmotionAvatarPresent(event) } returns false
        every { remoteStorage.downloadEmotionAvatar(event) } returns Completable.complete()

        // Act
        storageRepository.downloadEventFiles(event)
            .test()

        // Assert
        verifyAll {
            remoteStorage.downloadDetail(event.details[0]) wasNot Called
            remoteStorage.downloadDetail(event.details[1]) wasNot Called
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

        // Assert
        verify { remoteStorage.downloadEmotionAvatar(event) wasNot Called }
    }

//    @Test
//    fun getEvent_passesEventAndDetailCorrectlyToLocalAndRemoteStorage() {
//        val eventArg = slot<Event>()
//        val detailArg = slot<Detail>()
//        val event = EventFactory.makeEvent(numberOfDetails = 1, hasEmotionAvatar = true)
//        every { localStorage.isEmotionAvatarPresent(capture(eventArg)) } returns true
//        every { localStorage.isFilePresent(capture(detailArg)) } returns true
//        every { remoteStorage.downloadEmotionAvatar(capture(eventArg)) } returns Completable.complete()
//        every { remoteStorage.downloadDetail(capture(detailArg)) } returns Completable.complete()
//        val result = storageRepository.downloadEventFiles(event).test()
//        Assert.assertEquals(event, eventArg.captured)
//        Assert.assertEquals(event.details[0], detailArg.captured)
//        result.assertComplete()
//    }
//
//    @Test
//    fun getEvent_downloadsNothingIfAllFilesAlreadyInLocalStorage() {
//        val event = EventFactory.makeEvent(numberOfDetails = 1, hasEmotionAvatar = true)
//        every { localStorage.isEmotionAvatarPresent(any()) } returns true
//        every { localStorage.isFilePresent(any()) } returns true
//        every { remoteStorage.downloadEmotionAvatar(any()) } returns Completable.complete()
//        every { remoteStorage.downloadDetail(any()) } returns Completable.complete()
//        val result = storageRepository.downloadEventFiles(event).test()
//
//        verify(exactly = 1) { localStorage.isEmotionAvatarPresent(event) }
//        verify(exactly = 1) { localStorage.isFilePresent(event.details[0]) }
//        verify { remoteStorage.downloadEmotionAvatar(any()) wasNot called }
//        verify { remoteStorage.downloadDetail(any()) wasNot called }
//        result.assertComplete()
//    }
//
//    @Test
//    fun getEvent_downloadsFilesIfNotInLocalStorage() {
//        val event = EventFactory.makeEvent(numberOfDetails = 1, hasEmotionAvatar = true)
//        every { localStorage.isEmotionAvatarPresent(any()) } returns false
//        every { localStorage.isFilePresent(any()) } returns false
//        every { remoteStorage.downloadEmotionAvatar(any()) } returns Completable.complete()
//        every { remoteStorage.downloadDetail(any()) } returns Completable.complete()
//        val result = storageRepository.downloadEventFiles(event).test()
//        verify(exactly = 1) { localStorage.isEmotionAvatarPresent(event) }
//        verify(exactly = 1) { localStorage.isFilePresent(event.details[0]) }
//        verify(exactly = 1) { remoteStorage.downloadEmotionAvatar(event) }
//        verify(exactly = 1) { remoteStorage.downloadDetail(event.details[0]) }
//        result.assertComplete()
//    }
}
