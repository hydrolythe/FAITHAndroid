package be.hogent.faith.database.converters

import be.hogent.faith.database.models.DetailTypeEntity.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DetailTypeConverterTest{
    private val converter = DetailTypeConverter()

    @Test
    fun `DetailTypeConverter from id to enum converts correctly`() {
        assertEquals(TEXT, converter.toDetailTypeEntity(0))
        assertEquals(PICTURE, converter.toDetailTypeEntity(1))
        assertEquals(MUSIC, converter.toDetailTypeEntity(2))
        assertEquals(AUDIO, converter.toDetailTypeEntity(3))
        assertEquals(DRAWING, converter.toDetailTypeEntity(4))
        assertEquals(VIDEO, converter.toDetailTypeEntity(5))
    }

    @Test
    fun `DetailTypeConverter from enum to id converts correctly`() {
        assertEquals(0, converter.toInteger(TEXT))
        assertEquals(1, converter.toInteger(PICTURE))
        assertEquals(2, converter.toInteger(MUSIC))
        assertEquals(3, converter.toInteger(AUDIO))
        assertEquals(4, converter.toInteger(DRAWING))
        assertEquals(5, converter.toInteger(VIDEO))
    }
}