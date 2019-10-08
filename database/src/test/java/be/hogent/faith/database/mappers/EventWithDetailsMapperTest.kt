package be.hogent.faith.database.mappers

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class EventWithDetailsMapperTest {
    /*
    private val eventWithDetailsMapper = EventWithDetailsMapper()
    private val user = UserFactory.makeUser(0)

    @Test
    fun eventMapper_mapFromEntity_noDetails() {
        // Arrange
        val eventWithDetailsEntity = EntityFactory.makeEventWithDetailsEntity(DataFactory.randomUUID(), 0)

        // Act
        val resultingEvent = eventWithDetailsMapper.mapFromEntity(eventWithDetailsEntity)

        // Assert
        assertEqualEventData(eventWithDetailsEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapFromEntity_withDetails() {
        // Arrange EventEntity
        val eventWithDetailsEntity = EntityFactory.makeEventWithDetailsEntity()

        // Act
        val resultingEvent = eventWithDetailsMapper.mapFromEntity(eventWithDetailsEntity)

        // Assert
        assertEqualEventData(eventWithDetailsEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapToEntity_noDetails() {
        // Arrange
        val event = EventFactory.makeEvent(0)

        // Act
        val resultingEventEntity = eventWithDetailsMapper.mapToEntity(event, user.uuid)

        // Assert
        assertEqualEventData(resultingEventEntity, event)
    }

    @Test
    fun eventMapper_mapToEntity_withDetails() {
        // Arrange
        val event = EventFactory.makeEvent()

        // Act
        val resultingEventEntity = eventWithDetailsMapper.mapToEntity(event, user.uuid)

        // Assert
        assertEqualEventData(resultingEventEntity, event)
    }

    private fun assertEqualEventData(
        entity: EventWithDetails,
        model: Event
    ) {
        assertEquals(entity.eventEntity.uuid, model.uuid)
        assertEquals(entity.eventEntity.dateTime, model.dateTime)
        assertEquals(entity.eventEntity.title, model.title)

        assertEquals(entity.detailEntities.size, model.details.size)
        // Can't just use index because detailsList is actually a mix of three separate lists in the EventWithDetail
        entity.detailEntities.forEach { detailEntity ->
            val correspondingDetail = model.details.find { detailModel -> detailModel.uuid == detailEntity.uuid }
            assertEqualDetailsData(detailEntity, correspondingDetail!!, entity.eventEntity.uuid)
        }
    }

    private fun assertEqualDetailsData(
        entity: DetailEntity,
        model: Detail,
        entityUuid: UUID
    ) {
        assertEquals(entity.uuid, model.uuid)
        assertEquals(entity.file, model.file)
        assertEquals(entity.eventUuid, entityUuid)
    }

     */
}
