package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.IEventEncryptionService
import be.hogent.faith.database.firebase.EventDatabase
import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
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
        every { storageRepository.saveEventFiles(any()) } returns Single.just(mockk(relaxed = true))
        every { eventDatabase.insert(any(), any()) } returns Completable.complete()
        every { eventEncryptionService.encrypt(any()) } returns Single.just(mockk(relaxed = true))
    }

    @After
    fun clearMocks() {
        clearAllMocks()
    }

    @Test
    fun `When inserting an event its files are saved to storage`() {
        eventRepository.insert(event, user)
            .test()
            .assertComplete()
            .dispose()

        verify { storageRepository.saveEventFiles(any()) }
    }

    @Test
    fun `When inserting an event its data is encrypted`() {
        eventRepository.insert(event, user)
            .test()
            .assertComplete()
            .dispose()

        verify { eventEncryptionService.encrypt(event) }
    }

    @Test
    fun `When inserting an event its data is stored in the database`() {
        eventRepository.insert(event, user)
            .test()
            .assertComplete()
            .dispose()

        verify { eventDatabase.insert(any(), any()) }
    }
}