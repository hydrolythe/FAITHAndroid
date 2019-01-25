package be.hogent.faith.domain

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.EventLog
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EventLogTest {

    private lateinit var eventLog: EventLog

    @Before
    fun setUp() {
        eventLog = EventLog()
    }

    @Test
    fun `EventLog constructor starts with empty list of emotions`() {
        assert(eventLog.events.isEmpty())
    }

    @Test
    fun `EventLog addEmotion correctly adds the emotion`() {
        val emotion = mockk<Event>()

        eventLog.addEvent(emotion)

        assertEquals(1, eventLog.events.size)
        assertEquals(emotion, eventLog.events.first())
    }

    @Test
    fun `EventLog getGoals returns all goals`() {
        for (i in 1..10) {
            eventLog.addEvent(mockk())
        }

        assertEquals(10, eventLog.events.size)
    }
}