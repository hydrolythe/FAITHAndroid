package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.storage.ILocalStorageRepository
import be.hogent.faith.domain.models.User
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Test

class NewEventRepositoryImplTest {

    private val firebaseEventRepository = mockk<FirebaseEventRepository>(relaxed = true)
    private val userMapper = mockk<UserMapper>()
    private val eventMapper = mockk<EventMapper>()
    private val localStorageRepository = mockk<ILocalStorageRepository>()
    private val eventEncryptionService = mockk<EventEncryptionServiceInterface>(relaxed = true)

    private val eventRepository =
        EventRepositoryImpl(
            localStorageRepository,
            firebaseEventRepository,
            eventEncryptionService
        )

    private val user = UserFactory.makeUser(0)
    private val userEntity = EntityFactory.makeUserEntity()
    private val eventEntity = EntityFactory.makeEventEntityWithDetails(2)
    private val event = EventFactory.makeEvent(2)

    @After
    fun clearMocks() {
        clearAllMocks()
    }

    @Test
    fun `When inserting an event its data is saved in the device's local storage`() {
        slot<EncryptedEvent>().let {
            every { localStorageRepository.saveEvent(capture(it)) } returns Single.just(it.captured)
        }
        every { firebaseEventRepository.insert(any(), any()) } returns Completable.complete()
        every { eventEncryptionService.encrypt(any()) } returns mockk()

        eventRepository.insert(event, user)
            .test()
            .assertComplete()

        verify { localStorageRepository.saveEvent(any()) }
    }

    @Test
    fun `After inserting an event it is found in the device's online storage in an unreadable format`() {
        eventRepository.insert(event, user)
            .test()
            .assertComplete()
        // TODO make more specific?
        verify { firebaseEventRepository.insert(any(), any()) }
    }

    // TODO: failure cases

    private fun stubMapperFromEntity(model: EncryptedEvent, entity: EncryptedEventEntity) {
        every { eventMapper.mapFromEntity(entity) } returns model
    }

    private fun stubMapperToEntity(model: EncryptedEvent, entity: EncryptedEventEntity) {
        every { eventMapper.mapToEntity(model) } returns entity
    }

    private fun stubMapperToEntityUser(model: User, entity: UserEntity) {
        every { userMapper.mapToEntity(model) } returns entity
    }
}