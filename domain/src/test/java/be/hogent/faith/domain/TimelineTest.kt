package be.hogent.faith.domain

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TimelineTest {

    private lateinit var timeline: Timeline

    @Before
    fun setUp() {
        timeline = Timeline()
    }

    @Test
    fun `Timeline constructor starts with empty list of details`() {
        assert(timeline.events.isEmpty())
    }

    @Test
    fun `Timeline addEvent correctly adds the event`() {
        val event = mockk<Event>()

        timeline.addEvent(event)

        assertEquals(1, timeline.events.size)
        assertEquals(event, timeline.events.first())
    }

    @Test
    fun `Timeline getEvents returns all events`() {
        for (i in 1..10) {
            timeline.addEvent(mockk())
        }

        assertEquals(10, timeline.events.size)
    }
}