package be.hogent.faith.database.common

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.service.usecases.encryption.EncryptedDetail
import be.hogent.faith.service.usecases.encryption.EncryptedEvent
import be.hogent.faith.database.event.EventMapper
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.event.EncryptedEventEntity
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMapperTest {

    private val eventMapper = EventMapper
    private val user = UserFactory.makeUser()

    private val encryptedDetail = EncryptedDetail(
        file = DataFactory.randomFile(),
        fileName = DataFactory.randomString(),
        uuid = DataFactory.randomUUID(),
        type = "Encrypted type"
    )
    private val encryptedEventWithDetails = EncryptedEvent(
        dateTime = "encrypted datetime",
        title = "encrypted title",
        emotionAvatar = DataFactory.randomFile(),
        notes = "encrypted notes",
        uuid = DataFactory.randomUUID(),
        details = listOf(encryptedDetail),
        encryptedDEK = "encrypted version of DEK",
        encryptedStreamingDEK = "encrypted version of SDEK"
    )
    private val encryptedEventWithoutDetails = EncryptedEvent(
        dateTime = "encrypted datetime",
        title = "encrypted title",
        emotionAvatar = DataFactory.randomFile(),
        notes = "encrypted notes",
        uuid = DataFactory.randomUUID(),
        details = emptyList(),
        encryptedDEK = "encrypted version of DEK",
        encryptedStreamingDEK = "encrypted version of SDEK"
    )

    @Test
    fun eventMapper_mapFromEntity_noDetails() {
        val eventEntity = EntityFactory.makeEventEntity()
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)
        assertEqualData(eventEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapToEntity_noDetails() {
        val resultingEventEntity = eventMapper.mapToEntity(encryptedEventWithoutDetails)
        assertEqualData(resultingEventEntity, encryptedEventWithoutDetails)
    }

    @Test
    fun eventMapper_mapFromEntity_withDetails() {
        val eventEntity = EntityFactory.makeEventEntityWithDetails(5)
        val resultingEvent = eventMapper.mapFromEntity(eventEntity)
        assertEqualData(eventEntity, resultingEvent)
    }

    @Test
    fun eventMapper_mapToEntity_withDetails() {
        val resultingEventEntity = eventMapper.mapToEntity(encryptedEventWithDetails)
        assertEqualData(resultingEventEntity, encryptedEventWithDetails)
    }

    private fun assertEqualData(
        entity: EncryptedEventEntity,
        model: EncryptedEvent
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.dateTime, model.dateTime)
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
        entity: EncryptedDetailEntity,
        model: EncryptedDetail
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.file, FileConverter().toString(model.file))
        assertEquals(entity.type, model.type)
    }
}
