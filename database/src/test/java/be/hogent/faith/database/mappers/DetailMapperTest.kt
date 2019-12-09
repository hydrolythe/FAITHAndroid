package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.util.factory.DetailFactory
import be.hogent.faith.util.factory.EventFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailMapperTest {

    private val event = EventFactory.makeEvent()
    private val detailMapper = DetailMapper

    @Test
    fun `should map to Detail when DetailEntity is given`() {
        val entity = EntityFactory.makeDetailEntity()
        val model = detailMapper.mapFromEntity(entity)
        assertEqualData(entity, model)
    }

    @Test
    fun `should map to DetailEntity when Detail is given`() {
        val model = DetailFactory.makeRandomDetail()
        val entity = detailMapper.mapToEntity(model)
        assertEqualData(entity, model)
    }

    private fun assertEqualData(
        entity: DetailEntity,
        model: Detail
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.file, FileConverter().toString(model.file))
        when (model) {
            is AudioDetail -> assertEquals(DetailType.AUDIO, entity.type)
            is PhotoDetail -> assertEquals(DetailType.PHOTO, entity.type)
            is TextDetail -> assertEquals(DetailType.TEXT, entity.type)
            else -> assertEquals(DetailType.DRAWING, entity.type)
        }
    }
}