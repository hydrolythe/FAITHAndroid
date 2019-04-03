package be.hogent.faith.database.mappers

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.Event
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class EventWithDetailsMapperTest {
    private val eventWithDetailsMapper = EventWithDetailsMapper()
    private val user = UserFactory.makeUser(0)

    @Test
    fun eventMapper_mapFromEntity_noDetails() {
        // Arrange
        val eventWithDetailsEntity = EntityFactory.makeEventWithDetailsEntity(DataFactory.randomUUID(), 0)

        // Act
        val resultingEvent = eventWithDetailsMapper.mapFromEntity(eventWithDetailsEntity)

        // Assert
        assertEqualData(eventWithDetailsEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapFromEntity_withDetails() {
        // Arrange EventEntity
        val eventWithDetailsEntity = EntityFactory.makeEventWithDetailsEntity()

        // Act
        val resultingEvent = eventWithDetailsMapper.mapFromEntity(eventWithDetailsEntity)

        // Assert
        assertEqualData(eventWithDetailsEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapToEntity_noDetails() {
        // Arrange
        val event = EventFactory.makeEvent(0)

        // Act
        val resultingEventEntity = eventWithDetailsMapper.mapToEntity(event, user.uuid)

        // Assert
        assertEqualData(resultingEventEntity, event)
    }

    @Test
    fun eventMapper_mapToEntity_withDetails() {
        // Arrange
        val event = EventFactory.makeEvent()

        // Act
        val resultingEventEntity = eventWithDetailsMapper.mapToEntity(event, user.uuid)

        // Assert
        assertEqualData(resultingEventEntity, event)
    }

    private fun assertEqualData(
        entity: EventWithDetails,
        model: Event
    ) {
        with(entity.eventEntity) {
            assertEquals(uuid, model.uuid)
            assertEquals(dateTime, model.dateTime)
            assertEquals(title, model.title)
            assertEquals(entity.detailEntities.size, model.details.size)
            var i = 0
            entity.detailEntities.forEach {
                assertEqualDetailsData(it, model.details[i], uuid)
                i++
            }
        }
    }

    private fun assertEqualDetailsData(
        entity: DetailEntity,
        model: Detail,
        entityUuid: UUID
    ) {
        with(entity) {
            assertEquals(uuid, model.uuid)
            assertEquals(type.toString(), model.detailType.toString())
            assertEquals(eventUuid, entityUuid)
        }
    }
}
