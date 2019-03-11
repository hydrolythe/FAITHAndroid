package be.hogent.faith.database.repositories

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.mappers.EventMapper
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

class EventRepositoryImplTest {
    private val detailDao = mockk<DetailDao>()
    private val eventDao = mockk<EventDao>()
    private val database = mockk<EntityDatabase>()
    private val eventMapper = EventMapper()

    private lateinit var eventRepository: EventRepositoryImpl
    private val eventUuid = UUID.randomUUID()
    private val eventEntity = createEventEntity(eventUuid)
    private val detailEntities = createEventDetails(eventUuid)

    @Before
    fun setUp() {
        every { database.eventDao() } returns eventDao
        every { database.detailDao() } returns detailDao
        eventRepository = EventRepositoryImpl(database, eventMapper)
    }

    @Test
    fun eventRepository_existingEvent_succeeds() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.just(
            EventWithDetails(eventEntity, emptyList())
        )
        eventRepository.get(eventUuid)
            .test()
            .assertValue { it.uuid == eventUuid }
    }

    @Test
    fun eventRepository_nonExistingEvent_errors() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.empty()

        eventRepository.get(eventUuid)
            .test()
            .assertNoValues()
    }

    @Test
    fun eventRepository_eventWithDetails_returnsWithDetails() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.just(
            EventWithDetails(eventEntity, detailEntities)
        )

        // TODO: also test details themselves
        eventRepository.get(eventUuid)
            .test()
            .assertValue { event ->
                event.details.size == 2
            }
    }

    private fun createEventEntity(uuid: UUID): EventEntity {
        val time = LocalDateTime.of(2019, 10, 28, 7, 33)
        val description = "title"
        val emotionAvatarFile = File("path/to/event")
        return EventEntity(time, description, emotionAvatarFile, uuid)
    }

    private fun createEventDetails(eventUuid: UUID): List<DetailEntity> {
        val details = mutableListOf<DetailEntity>()
        for (i in 1..2) {
            details.add(
                DetailEntity(
                    DetailTypeEntity.DRAWING,
                    File("path/to/detail$i"),
                    UUID.randomUUID(),
                    eventUuid
                )
            )
        }
        return details
    }
}