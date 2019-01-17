package be.hogent.faith.database.converters

import be.hogent.faith.database.models.DetailTypeEntity
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DetailTypeConverterTest {
    private val converter = DetailTypeConverter()

    @Test
    fun `DetailTypeConverter from id to enum converts correctly`() {
        assertEquals(DetailTypeEntity.TEXT, converter.toDetailTypeEntity(0))
        assertEquals(DetailTypeEntity.PICTURE, converter.toDetailTypeEntity(1))
        assertEquals(DetailTypeEntity.MUSIC, converter.toDetailTypeEntity(2))
        assertEquals(DetailTypeEntity.AUDIO, converter.toDetailTypeEntity(3))
        assertEquals(DetailTypeEntity.DRAWING, converter.toDetailTypeEntity(4))
        assertEquals(DetailTypeEntity.VIDEO, converter.toDetailTypeEntity(5))
    }

    @Test
    fun `DetailTypeConverter from enum to id converts correctly`() {
        assertEquals(0, converter.toInteger(DetailTypeEntity.TEXT))
        assertEquals(1, converter.toInteger(DetailTypeEntity.PICTURE))
        assertEquals(2, converter.toInteger(DetailTypeEntity.MUSIC))
        assertEquals(3, converter.toInteger(DetailTypeEntity.AUDIO))
        assertEquals(4, converter.toInteger(DetailTypeEntity.DRAWING))
        assertEquals(5, converter.toInteger(DetailTypeEntity.VIDEO))
    }
}