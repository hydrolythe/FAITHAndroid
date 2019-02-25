package be.hogent.faith.domain

import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.Event
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month

class EventTest {

    private lateinit var event: Event

    @Before
    fun setUp() {
        event = Event(mockk(), "testTitle")
    }

    @Test
    fun `Event constructor correctly sets attributes`() {
        val date = LocalDateTime.of(1991, Month.OCTOBER, 28, 8, 33)
        val title = "title"

        val newEvent = Event(date, title)

        assertEquals(title, newEvent.title)
        assertEquals(date, newEvent.dateTime)
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