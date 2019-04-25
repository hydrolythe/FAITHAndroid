package be.hogent.faith.database.repositories

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.factory.UserFactory
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.EventWithDetailsMapper
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class EventRepositoryImplTest {
    private val detailDao = mockk<DetailDao>()
    private val eventDao = mockk<EventDao>()
    private val database = mockk<EntityDatabase>(relaxed = true)
    private val detailMapper = mockk<DetailMapper>()
    private val eventMapper = mockk<EventMapper>()
    private val eventWithDetailsMapper = mockk<EventWithDetailsMapper>()

    private val eventRepository = EventRepositoryImpl(database, eventMapper, eventWithDetailsMapper, detailMapper)

    private val user = UserFactory.makeUser(0)
    private val eventWithDetails = EntityFactory.makeEventWithDetailsEntity(user.uuid, 2)
    private val eventUuid = eventWithDetails.eventEntity.uuid
    private val event = EventFactory.makeEvent(2)

    @Before
    fun setUp() {
        every { database.eventDao() } returns eventDao
        every { database.detailDao() } returns detailDao
    }

    @Test
    fun eventRepository_existingEvent_succeeds() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.just(
            eventWithDetails
        )
        stubMapperFromEntity(event, eventWithDetails)
        eventRepository.get(eventUuid)
            .test()
            .assertValue(event)
    }

    @Test
    fun eventRepository_nonExistingEvent_errors() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.empty()

        eventRepository.get(eventUuid)
            .test()
            .assertNoValues()
    }

    @Test
    fun eventRepository_deleteEventCompletes() {
        every { eventDao.delete(eventWithDetails.eventEntity) } returns Completable.complete()
        stubMapperToEntity(event, eventWithDetails.eventEntity)
        eventRepository.delete(event, user).test().assertComplete()
    }

    @Test
    fun eventRepository_getAll_succeeds() {
        every { eventDao.getAllEventsWithDetails(user.uuid) } returns Flowable.just(listOf(eventWithDetails))
        stubMapperFromEntities(listOf(event), listOf(eventWithDetails))
        eventRepository.getAll(user).test().assertValue(listOf(event))
    }

    private fun stubMapperFromEntity(model: Event, entity: EventWithDetails) {
        every { eventWithDetailsMapper.mapFromEntity(entity) } returns model
    }

    private fun stubMapperFromEntities(models: List<Event>, entities: List<EventWithDetails>) {
        every { eventWithDetailsMapper.mapFromEntities(entities) } returns models
    }

    private fun stubMapperToEntity(model: Event, entity: EventEntity) {
        every { eventMapper.mapToEntity(model, user.uuid) } returns entity
    }
}