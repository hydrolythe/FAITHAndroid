package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {

    private val eventMapper = EventMapper
    private val user = UserFactory.makeUser()

    @Test
    fun eventMapper_mapFromEntity_noDetails() {
        val eventEntity = EntityFactory.makeEventEntity()
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)
        assertEqualData(eventEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapToEntity_noDetails() {
        val event = EventFactory.makeEvent(0)
        val resultingEventEntity = eventMapper.mapToEntity(event)
        assertEqualData(resultingEventEntity, event)
    }

    @Test
    fun eventMapper_mapFromEntity_withDetails() {
        val eventEntity = EntityFactory.makeEventEntityWithDetails(5)
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)
        assertEqualData(eventEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapToEntity_withDetails() {
        val event = EventFactory.makeEvent(5)
        val resultingEventEntity = eventMapper.mapToEntity(event)
        assertEqualData(resultingEventEntity, event)
    }

    private fun assertEqualData(
        entity: EventEntity,
        model: Event
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.dateTime, LocalDateTimeConverter().toString(model.dateTime))
        assertEquals(entity.title, model.title)
        assertEquals(
            entity.emotionAvatar,
            model.emotionAvatar?.let { FileConverter().toString(it) })
        assertEquals(entity.details.count(), model.details.count())
        entity.details.forEach { detailEntity ->
            val correspondingDetail =
                model.details.find { detailModel -> detailModel.uuid.toString() == detailEntity.uuid }
            assertEqualDetailsData(detailEntity, correspondingDetail!!)
        }
    }

    private fun assertEqualDetailsData(
        entity: DetailEntity,
        model: Detail
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.file, FileConverter().toString(model.file))
        assertEquals(entity.name, model.name)
        when (model) {
            is AudioDetail -> assertEquals(DetailType.AUDIO, entity.type)
            is PhotoDetail -> assertEquals(DetailType.PHOTO, entity.type)
            is TextDetail -> assertEquals(DetailType.TEXT, entity.type)
            else -> assertEquals(DetailType.DRAWING, entity.type)
        }
    }
}
