package be.hogent.faith.database.repositories

import be.hogent.faith.database.encryption.EventEncryptionServiceInterface
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.storage.localStorage.ILocalStorageRepository
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test

class NewEventRepositoryImplTest {

    private val firebaseEventRepository = mockk<FirebaseEventRepository>(relaxed = true)
    private val userMapper = mockk<UserMapper>()
    private val eventMapper = mockk<EventMapper>()
    private val localStorageRepository = mockk<ILocalStorageRepository>()
    private val eventEncrypter = mockk<EventEncryptionServiceInterface>(relaxed = true)

    private val eventRepository =
        EventRepositoryImpl(
            userMapper,
            eventMapper,
            localStorageRepository,
            firebaseEventRepository,
            eventEncrypter
        )

    private val user = UserFactory.makeUser(0)
    private val userEntity = EntityFactory.makeUserEntity()
    private val eventEntity = EntityFactory.makeEventEntityWithDetails(2)
    private val event = EventFactory.makeEvent(2)
    private val uuid = eventEntity.uuid

    @Before
    fun setUpMocks() {
        stubMapperToEntity(event, eventEntity)
        stubMapperFromEntity(event, eventEntity)
        stubMapperToEntityUser(user, userEntity)
    }

    @After
    fun clearMocks() {
        clearAllMocks()
    }

    @Test
    fun `After inserting an event its data was saved in the device's local storage in an encrypted format`() {
        every { localStorageRepository.saveEvent(event) } returns Single.just(event)
        eventRepository.insert(event, user)
            .test()
            .assertComplete()

        verify { localStorageRepository.saveEvent(event) }
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

    private fun stubMapperFromEntity(model: Event, entity: EncryptedEventEntity) {
        every { eventMapper.mapFromEncryptedEntity(entity) } returns model
    }

    private fun stubMapperFromEntities(models: List<Event>, entities: List<EncryptedEventEntity>) {
        every { eventMapper.mapFromEntities(entities) } returns models
    }

    private fun stubMapperToEntity(model: Event, entity: EncryptedEventEntity) {
        every { eventMapper.mapToEncryptedEntity(model) } returns entity
    }

    private fun stubMapperToEntityUser(model: User, entity: UserEntity) {
        every { userMapper.mapToEntity(model) } returns entity
    }
}