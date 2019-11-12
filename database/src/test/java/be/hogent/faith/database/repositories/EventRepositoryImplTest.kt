package be.hogent.faith.database.repositories

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.firebase.FirebaseEventRepository
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.Maybe
import org.junit.Test
import java.util.UUID

class EventRepositoryImplTest {

    private val firebaseEventRepository = mockk<FirebaseEventRepository>(relaxed = true)
    private val userMapper = mockk<UserMapper>()
    private val eventMapper = mockk<EventMapper>()

    private val eventRepository =
        EventRepositoryImpl(userMapper, eventMapper, firebaseEventRepository)

    private val user = UserFactory.makeUser(0)
    private val userEntity = EntityFactory.makeUserEntity()
    private val eventEntity = EntityFactory.makeEventEntityWithDetails(2)
    private val event = EventFactory.makeEvent(2)
    private val uuid = eventEntity.uuid

    @Test
    fun eventRepository_get_existingEvent_succeeds() {
        every { firebaseEventRepository.get(uuid) } returns Flowable.just(
            eventEntity
        )
        stubMapperFromEntity(event, eventEntity)
        eventRepository.get(UUID.fromString(uuid))
            .test()
            .assertValue(event)
    }

    @Test
    fun eventRepository_get_nonExistingEvent_errors() {
        every { firebaseEventRepository.get(uuid) } returns Flowable.empty()

        eventRepository.get(UUID.fromString(uuid))
            .test()
            .assertNoValues()
    }

    @Test
    fun eventRepository_insert_succeeds() {
        every { firebaseEventRepository.insert(eventEntity, userEntity) } returns Maybe.just(
            eventEntity
        )
        stubMapperToEntity(event, eventEntity)
        stubMapperFromEntity(event, eventEntity)
        stubMapperToEntityUser(user, userEntity)
        eventRepository.insert(event, user)
            .test()
            .assertValue { it.uuid == event.uuid && it.details.count() == 2 }
    }

    @Test
    fun eventRepository_insert_userNotAuthenticated_errors() {
        every { firebaseEventRepository.insert(eventEntity, userEntity) } returns Maybe.error(
            RuntimeException("Unauthorized used.")
        )

        eventRepository.get(UUID.fromString(uuid))
            .test()
            .assertNoValues()
    }

    @Test
    fun eventRepository_getAll_authenticatedUser_succeeds() {
        // simulates 2 lists on the stream
        every { firebaseEventRepository.getAll() } returns Flowable.just(
            listOf(
                eventEntity,
                eventEntity
            )
        )
        stubMapperFromEntities(listOf(event, event), listOf(eventEntity, eventEntity))
        eventRepository.getAll()
            .test()
            .assertValue(listOf(event, event))
    }

    @Test
    fun eventRepository_getAll_2listsOnTheStream_authenticatedUser_succeeds() {
        // simulates 2 lists on the stream
        every { firebaseEventRepository.getAll() } returns Flowable.just(
            listOf(
                eventEntity,
                eventEntity
            ),
            listOf(
                eventEntity,
                eventEntity
            )
        )
        stubMapperFromEntities(listOf(event, event), listOf(eventEntity, eventEntity))
        eventRepository.getAll()
            .test()
            .assertValueCount(2)
    }

    private fun stubMapperFromEntity(model: Event, entity: EventEntity) {
        every { eventMapper.mapFromEntity(entity) } returns model
    }

    private fun stubMapperFromEntities(models: List<Event>, entities: List<EventEntity>) {
        every { eventMapper.mapFromEntities(entities) } returns models
    }

    private fun stubMapperToEntity(model: Event, entity: EventEntity) {
        every { eventMapper.mapToEntity(model) } returns entity
    }

    private fun stubMapperToEntityUser(model: User, entity: UserEntity) {
        every { userMapper.mapToEntity(model) } returns entity
    }
}