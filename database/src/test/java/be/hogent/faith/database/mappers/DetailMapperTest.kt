package be.hogent.faith.database.mappers

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.util.factory.DetailFactory
import be.hogent.faith.util.factory.EventFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class DetailMapperTest {
    private val event = EventFactory.makeEvent()
    private val detailMapper = DetailMapper

    @Test
    fun `should map to Detail when DetailEntity is given`() {
        val entity = EntityFactory.makeDetailEntity(event.uuid)
        val model = detailMapper.mapFromEntity(entity)
        assertEqualData(entity, model, event.uuid)
    }

    @Test
    fun `should map to DetailEntity when Detail is given`() {
        val model = DetailFactory.makeDetail()
        val entity = detailMapper.mapToEntity(model, event.uuid)
        assertEqualData(entity, model, event.uuid)
    }

    private fun assertEqualData(
        entity: DetailEntity,
        model: Detail,
        entityUuid: UUID
    ) {
        assertEquals(entity.uuid, model.uuid)
        assertEquals(entity.file, model.file)
        assertEquals(entity.eventUuid, entityUuid)
    }
}