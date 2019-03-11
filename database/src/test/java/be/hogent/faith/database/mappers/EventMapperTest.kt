package be.hogent.faith.database.mappers

import be.hogent.faith.database.factory.EventFactory
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {
    private val eventMapper = EventMapper()

    @Test
    fun eventMapper_mapFromEntity_noDetails() {
        // Arrange
        val eventEntity = EventFactory.makeEventEntity()
        // Act
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)
        // Assert
        assertEqualData(eventEntity, resultingEvent)
        assert(resultingEvent.details.isEmpty())
    }

    @Test
    fun eventMapper_mapToEntity_noDetails() {
        // Arrange
        val event = EventFactory.makeEvent(0)
        // Act
        val resultingEventEntity = eventMapper.mapToEntity(event)
        // Assert
        assertEqualData(resultingEventEntity, event)
    }

    private fun assertEqualData(
        entity: EventEntity,
        model: Event
    ) {
        assertEquals(entity.uuid, model.uuid)
        assertEquals(entity.dateTime, model.dateTime)
        assertEquals(entity.title, model.title)
    }
}