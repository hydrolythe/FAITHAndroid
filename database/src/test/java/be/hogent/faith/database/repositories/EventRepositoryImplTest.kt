package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.encryption.IEventEncryptionService
import be.hogent.faith.database.firebase.EventDatabase
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventRepositoryImplTest {

    private val storageRepository = mockk<IFileStorageRepository>()
    private val eventDatabase = mockk<EventDatabase>()
    private val eventEncryptionService = mockk<IEventEncryptionService>()

    private val eventRepository =
        EventRepositoryImpl(
            storageRepository,
            eventDatabase,
            eventEncryptionService
        )

    private val user = UserFactory.makeUser(numberOfEvents = 0)
    private val event = EventFactory.makeEvent(numberOfDetails = 2)

    @Before
    fun setUpMocks() {
        mockkObject(EventMapper)
        mockkObject(UserMapper)
    }

    @After
    fun clearMocks() {
        clearAllMocks()
    }

    @Test
    fun `When inserting an event its files are saved to storage`() {
        // Arrange
        every { storageRepository.saveEventFiles(any()) } returns Single.just(mockk(relaxed = true))
        every { eventDatabase.insert(any(), any()) } returns Completable.complete()
        every { eventEncryptionService.encrypt(event) } returns Single.just(mockk())

        eventRepository.insert(event, user)
            .test()
            .assertComplete()
            .dispose()

        verify { storageRepository.saveEventFiles(any()) }
    }

    @Test
    fun `When inserting an event its data is encrypted`() {
        // Arrange
        every { storageRepository.saveEventFiles(any()) } returns Single.just(mockk(relaxed = true))
        every { eventDatabase.insert(any(), any()) } returns Completable.complete()
        every { eventEncryptionService.encrypt(event) } returns Single.just(mockk())

        eventRepository.insert(event, user)
            .test()
            .assertComplete()
            .dispose()

        verify { eventEncryptionService.encrypt(event) }
    }

    @Test
    fun `When inserting an event its data is stored in the database`() {
        // Arrange
        every { storageRepository.saveEventFiles(any()) } returns Single.just(mockk(relaxed = true))
        every { eventDatabase.insert(any(), any()) } returns Completable.complete()
        every { eventEncryptionService.encrypt(event) } returns Single.just(mockk())

        eventRepository.insert(event, user)
            .test()
            .assertComplete()
            .dispose()

        verify { eventDatabase.insert(any(), any()) }
    }

    @Test
    fun `when requesting an events data and the database holds the requested event, it is returned`() {
        // Arrange
        val encryptedEventEntity = mockk<EncryptedEventEntity>(relaxed = true)
        val encryptedEvent = mockk<EncryptedEvent>()
        every { eventDatabase.get(event.uuid) } returns Observable.just(encryptedEventEntity)
        every { EventMapper.mapFromEntity(encryptedEventEntity) } returns encryptedEvent
        every { eventEncryptionService.decryptData(encryptedEvent) } returns Single.just(event)

        // Act
        eventRepository.getEventData(event.uuid)
            .test()
            .assertValue { receivedEvent ->
                receivedEvent == event
            }
            .dispose()
    }

    @Test
    fun `when requesting an events data andthe database does not hold the requested event, an error is returned`() {
        // Arrange
        every { eventDatabase.get(event.uuid) } returns Observable.error(mockk<Throwable>())

        // Act
        eventRepository.getEventData(event.uuid)
            .test()
            .assertError(Throwable::class.java)
            .dispose()
    }

    @Test
    fun `when requesting an event's files and the files are already available, nothing has to be downloaded`() {
        // Arrange
        every { storageRepository.filesReadyToUse(event) } returns true

        // Act
        eventRepository.makeEventFilesAvailable(event)
            .test()
            .assertComplete()
            .dispose()

        verify { storageRepository.downloadEventFiles(event) wasNot Called }
    }

    @Test
    fun `when requesting an event's files and they are on the device but in an encrypted format, they are not downloaded again`() {
        // Act
        eventRepository.getEventData(event.uuid)
            .test()
            .assertValue { receivedEvent ->
                receivedEvent == event
            }
            .dispose()
    }

    @Test
    fun `when an events files are already downloaded, nothing has to be downloaded`() {
    }

    @Test
    fun `when an events files are downloaded, they are decrypted`() {
    }
}