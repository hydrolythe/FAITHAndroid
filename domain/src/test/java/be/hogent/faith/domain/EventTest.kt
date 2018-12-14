package be.hogent.faith.domain

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

class EventTest {

    private lateinit var event: Event

    @Before
    fun setUp() {
        event = Event(mockk(), "testDescription", mockk())
    }

    @Test
    fun `Event constructor correctly sets attributes`() {
        val date = LocalDate.of(1991, Month.OCTOBER, 28)
        val description = "testDescription"
        val category = Category.FRIENDS

        val newEvent = Event(date, description, category)

        assertEquals(description, newEvent.description)
        assertEquals(date, newEvent.date)
        assertEquals(category, newEvent.category)
    }

    @Test
    fun `Event addDetail correctly adds the detail`() {
        val detail = mockk<Detail>()

        event.addDetail(detail)

        assertEquals(1, event.details.size)
        assertEquals(detail, event.details.first())
    }

    @Test
    fun `Event getDetails returns all details`() {
        for (i in 1..10) {
            event.addDetail(mockk())
        }

        assertEquals(10, event.details.size)
    }
}