package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month

class EventDateFilterTest {
    private val event1 = Event(dateTime = LocalDateTime.of(2020, Month.FEBRUARY, 3, 10, 30))
    private val event2 = Event(dateTime = LocalDateTime.of(2020, Month.FEBRUARY, 5, 10, 0))
    private val event3 = Event(dateTime = LocalDateTime.of(2020, Month.MARCH, 2, 10, 30))
    private val events = listOf(event1, event2, event3)

    private lateinit var filter: EventDateFilter

    @Test
    fun `when the filters start date is exactly the day of an event, it is let through`() {
        // Arrange
        filter = EventDateFilter(
            startDate = LocalDate.of(2020, Month.FEBRUARY, 3),
            endDate = LocalDate.of(2020, Month.FEBRUARY, 20)
        )

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertEquals(2, filteredEvents.size)
        assertTrue(filteredEvents.contains(event1))
        assertTrue(filteredEvents.contains(event2))
    }

    @Test
    fun `when the filters end date is exactly the day of an event, it is let through`() {
        // Arrange
        filter = EventDateFilter(
            startDate = LocalDate.of(2020, Month.FEBRUARY, 1),
            endDate = LocalDate.of(2020, Month.FEBRUARY, 5)
        )

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertEquals(2, filteredEvents.size)
        assertTrue(filteredEvents.contains(event1))
        assertTrue(filteredEvents.contains(event2))
    }

    @Test
    fun `when the filters end day is exactly the day before the time of an event, it is not let through`() {
        // Arrange
        filter = EventDateFilter(
            startDate = LocalDate.of(2020, Month.FEBRUARY, 1),
            endDate = LocalDate.of(2020, Month.FEBRUARY, 4)
        )

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event1))
    }

    @Test
    fun `when the filters start date is exactly the day after the time of an event, it is not let through`() {
        // Arrange
        filter = EventDateFilter(
            startDate = LocalDate.of(2020, Month.FEBRUARY, 4),
            endDate = LocalDate.of(2020, Month.FEBRUARY, 8)
        )

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertEquals(1, filteredEvents.size)
        assertTrue(filteredEvents.contains(event2))
    }

    @Test
    fun `when the event is in between the filters start and end date, it is let through`() {
        // Arrange
        filter = EventDateFilter(
            startDate = LocalDate.of(2020, Month.JANUARY, 1),
            endDate = LocalDate.of(2020, Month.DECEMBER, 31)
        )

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertEquals(3, filteredEvents.size)
        assertTrue(filteredEvents.contains(event1))
        assertTrue(filteredEvents.contains(event2))
        assertTrue(filteredEvents.contains(event3))
    }
}