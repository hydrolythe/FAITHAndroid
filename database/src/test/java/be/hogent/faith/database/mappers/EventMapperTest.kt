package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

class EventMapperTest {
    private val eventMapper = EventMapper()

    @Test
    fun eventMapper_mapFromEntity_noDetails() {
        // Arrange
        val uuid = UUID.randomUUID()
        val time = LocalDateTime.of(2019, 10, 28, 7, 33)
        val title = "title"
        val emotionAvatarFile = File("path/to/file")
        val eventEntity = EventEntity(time, title, emotionAvatarFile, uuid)

        // Act
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)

        // Assert
        assertEquals(uuid, resultingEvent.uuid)
        assertEquals(time, resultingEvent.dateTime)
        assertEquals(title, resultingEvent.title)
        assertEquals(emotionAvatarFile.path, resultingEvent.emotionAvatar!!.path)
        assert(resultingEvent.details.isEmpty())
    }

    @Test
    fun eventMapper_mapFromEntity_withDetails() {
        // Arrange EventEntity
        val uuid = UUID.randomUUID()
        val time = LocalDateTime.of(2019, 10, 28, 7, 33)
        val title = "title"
        val emotionAvatarFile = File("path/to/emotionAvatarFile")
        val eventEntity = EventEntity(time, title, emotionAvatarFile, uuid)
        // Arrange DetailEntities
        val detailUUID = UUID.randomUUID()
        val detailType = DetailTypeEntity.AUDIO
        val detailFile = File("path/to/detailFile")
        val detailEntity = DetailEntity(detailType, detailFile, detailUUID, eventEntity.uuid)
        eventEntity.details.add(detailEntity)

        // Act
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)

        // Assert
        assertEquals(1, resultingEvent.details.size)
        // Checking if the details inside the event are the same is done in the DetailEntityMapperTest
    }

    @Test
    fun eventMapper_mapToEntity_noDetails() {
        // Arrange
        val uuid = UUID.randomUUID()
        val time = LocalDateTime.of(2019, 10, 28, 7, 33)
        val title = "title"
        val emotionAvatarFile = File("path/to/file")
        val event = Event(time, title, emotionAvatarFile, uuid)

        // Act
        val resultingEventEntity = eventMapper.mapToEntity(event)

        // Assert
        assertEquals(uuid, resultingEventEntity.uuid)
        assertEquals(time, resultingEventEntity.dateTime)
        assertEquals(title, resultingEventEntity.title)
        assertEquals(emotionAvatarFile.path, resultingEventEntity.emotionAvatar!!.path)
        assert(resultingEventEntity.details.isEmpty())
    }

    @Test
    fun eventMapper_mapToEntity_withDetails() {
        // Arrange Event
        val uuid = UUID.randomUUID()
        val time = LocalDateTime.of(2019, 10, 28, 7, 33)
        val title = "title"
        val emotionAvatarFile = File("path/to/file")
        val event = Event(time, title, emotionAvatarFile, uuid)
        // Arrange Detail
        val detailType = DetailType.AUDIO
        val detailFile = File("path/to/detailFile")
        val detail = Detail(detailType, detailFile, event.uuid)
        event.addDetail(detail)

        // Act
        val resultingEventEntity = eventMapper.mapToEntity(event)

        // Assert
        assertEquals(1, resultingEventEntity.details.size)
    }
}