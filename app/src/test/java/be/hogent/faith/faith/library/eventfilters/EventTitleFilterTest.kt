package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event
import junit.framework.TestCase.assertTrue
import org.junit.Test

class EventTitleFilterTest {

    private val event1 = Event(title = "Title with test the word")
    private val event2 = Event(title = "Title no the word")
    private val event3 = Event(title = "test the title word")
    private val events = listOf(event1, event2, event3)

    private lateinit var filter: EventTitleFilter

    @Test
    fun `passing a single word through the filter leaves all events with that word in the title`() {
        // Arrange
        filter = EventTitleFilter("test")

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertTrue(filteredEvents.contains(event1))
        assertTrue(filteredEvents.contains(event3))
    }

    @Test
    fun `passing multiple words through the filter leaves all events with all those words in that order in the title`() {
        // Arrange
        filter = EventTitleFilter("the word")

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertTrue(filteredEvents.contains(event1))
        assertTrue(filteredEvents.contains(event2))
    }

    @Test
    fun `passing multiple words through the filter leaves all events with all those words in the title, not necessarily in the given order`() {
        // Arrange
        filter = EventTitleFilter("word test")

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertTrue(filteredEvents.contains(event1))
        assertTrue(filteredEvents.contains(event3))
    }

    @Test
    fun `passing a word through the filter that is not in the events title means the event does not pass`() {
        // Arrange
        filter = EventTitleFilter("asdlfkjasd")

        // Act
        val filteredEvents = events.filter(filter)

        // Assert
        assertTrue(filteredEvents.isEmpty())
    }
}