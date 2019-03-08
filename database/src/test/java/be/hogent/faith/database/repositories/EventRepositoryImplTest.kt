package be.hogent.faith.database.repositories

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.factory.EventFactory
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.mappers.EventWithDetailsMapper
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Test

class EventRepositoryImplTest {
    private val detailDao = mockk<DetailDao>()
    private val eventDao = mockk<EventDao>()
    private val eventMapper = mockk<EventMapper>()
    private val eventWithDetailsMapper = mockk<EventWithDetailsMapper>()

    private val eventRepository = EventRepositoryImpl(eventDao, detailDao, eventMapper, eventWithDetailsMapper)

    private val eventWithDetails = EventFactory.makeEventWithDetailsEntity(2)
    private val eventUuid = eventWithDetails.eventEntity.uuid
    private val event = EventFactory.makeEvent(2)

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
    fun deleteEventCompletes() {
        every { eventDao.delete(eventWithDetails.eventEntity) } returns Completable.complete()
        stubMapperToEntity(event, eventWithDetails.eventEntity)
        eventRepository.delete(event).test().assertComplete()
    }

    @Test
    fun getAll_succeeds() {
        every { eventDao.getAllEventsWithDetails() } returns Flowable.just(listOf(eventWithDetails))
        stubMapperFromEntities(listOf(event), listOf(eventWithDetails))
        eventRepository.getAll().test().assertValue(listOf(event))
    }

    private fun stubMapperFromEntity(model: Event, entity: EventWithDetails) {
        every { eventWithDetailsMapper.mapFromEntity(entity) } returns model
    }

    private fun stubMapperFromEntities(models: List<Event>, entities: List<EventWithDetails>) {
        every { eventWithDetailsMapper.mapFromEntities(entities) } returns models
    }


    private fun stubMapperToEntity(model: Event, entity: EventEntity) {
        every { eventMapper.mapToEntity(model) } returns entity
    }

}