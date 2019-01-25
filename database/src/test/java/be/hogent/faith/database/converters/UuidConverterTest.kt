package be.hogent.faith.database.converters

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.UUID

class UuidConverterTest {
    private val converter = UuidConverter()

    @Test
    fun `UuidConverter from string to uuid converts correctly`() {
        val uuidString = "d883853b-7b23-401f-816b-ed4231e6dd6a"

        assertEquals(UUID.fromString(uuidString), converter.toUuid(uuidString))
    }
    @Test
    fun `UuidConverter from uuid to string converts correctly`() {
        val uuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")

        assertEquals("d883853b-7b23-401f-816b-ed4231e6dd6a", converter.toString(uuid))
    }
}