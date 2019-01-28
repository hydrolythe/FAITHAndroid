package be.hogent.faith.database.converters

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDateTime

class LocalDateTimeConverterTest {
    private val converter = LocalDateTimeConverter()

    @Test
    fun `LocalDateTimeConverter from dateTime to String converts correctly`() {
        //Arrange
        val localDateTime = LocalDateTime.of(2018,10,28,8,30)

        //Act
        val result = converter.toString(localDateTime)

        //Assert
        assertEquals("2018-10-28T08:30:00", result)

    }

    @Test
    fun `LocalDateTimeConverter from String to dateTime converts correctly`() {
        //Arrange
        val dateTimeString = "2011-12-03T10:15:30"

        //Act
        val result = converter.toDate(dateTimeString)

        //Assert
        assertEquals(result, LocalDateTime.of(2011,12, 3, 10,15,30))

    }
}
