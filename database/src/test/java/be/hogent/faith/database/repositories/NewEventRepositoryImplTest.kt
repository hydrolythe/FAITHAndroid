package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Test

class NewEventRepositoryImplTest {

    private val storageRepository = mockk<IFileStorageRepository>()
    private val firebaseEventRepository = mockk<FirebaseEventRepository>()
    private val eventEncryptionService = mockk<EventEncryptionServiceInterface>()

    private val eventRepository =
        EventRepositoryImpl(
            storageRepository,
            firebaseEventRepository,
            eventEncryptionService
        )

    private val user = UserFactory.makeUser(numberOfEvents = 0)
    private val event = EventFactory.makeEvent(numberOfDetails = 2)

    @After
    fun clearMocks() {
        clearAllMocks()
    }

    @Test
    fun `When inserting an event its data is saved to storage`() {
        val eventSlot = slot<EncryptedEvent>()
        every { storageRepository.saveEvent(capture(eventSlot)) } returns Single.just(eventSlot.captured)

        every { eventEncryptionService.encrypt(any()) } returns mockk()

        eventRepository.insert(event, user)
            .test()
            .assertComplete()

        verify { storageRepository.saveEvent(any()) }
    }

    @Test
    fun `When inserting an event its data is encrypted`() {
        eventRepository.insert(event, user)
            .test()
            .assertComplete()
        // TODO make more specific?
        verify { eventEncryptionService.encrypt(event) }
    }

    // TODO: failure cases
}