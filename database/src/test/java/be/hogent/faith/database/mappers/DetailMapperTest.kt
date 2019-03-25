package be.hogent.faith.database.mappers

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.util.factory.DetailFactory
import be.hogent.faith.util.factory.EventFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class DetailMapperTest {
    private val event = EventFactory.makeEvent()
    private val mapper = DetailMapper()

    @Test
    fun `should map to Detail when DetailEntity is given`() {
        val entity = EntityFactory.makeDetailEntity(event.uuid)
        val model = mapper.mapFromEntity(entity)
        assertEqualData(entity, model, event.uuid)
    }

    @Test
    fun `should map to DetailEntity when Detail is given`() {
        val model = DetailFactory.makeDetail()
        val entity = mapper.mapToEntity(model, event.uuid)
        assertEqualData(entity, model, event.uuid)
    }

    private fun assertEqualData(
        entity: DetailEntity,
        model: Detail,
        entityUuid: UUID
    ) {
        assertEquals(entity.uuid, model.uuid)
        assertEquals(entity.type.toString(), model.detailType.toString())
        assertEquals(entity.eventUuid, entityUuid)
    }
}