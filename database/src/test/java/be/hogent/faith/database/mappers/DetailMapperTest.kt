package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.util.factory.DataFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class DetailMapperTest {

    private val detailMapper = DetailMapper

    @Test
    fun `should map to EncryptedDetail when EncryptedDetailEntity is given`() {
        val entity = EntityFactory.makeDetailEntity()
        val model = detailMapper.mapFromEntity(entity)
        assertEqualData(entity, model)
    }

    @Test
    fun `should map to DetailEntity when Detail is given`() {
        val model = EncryptedDetail(
            file = DataFactory.randomFile(),
            uuid = UUID.randomUUID(),
            type = "encrypted type"
        )
        val entity = detailMapper.mapToEntity(model)
        assertEqualData(entity, model)
    }

    private fun assertEqualData(
        entity: EncryptedDetailEntity,
        model: EncryptedDetail
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.file, FileConverter().toString(model.file))
        assertEquals(entity.type, model.type)
    }
}