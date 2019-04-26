package be.hogent.faith.domain

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

class UserTest {
    private lateinit var user: User

    @Before
    fun setUp() {
        user = User("username", "R.drawable.avatar")
    }

    @Test
    fun user_addsEvent() {
        val event = mockk<Event>()

        user.addEvent(event)

        Assert.assertEquals(1, user.events.size)
        Assert.assertEquals(event, user.events.first())
    }

    @Test
    fun user_returnsAllDetails() {
        for (i in 1..10) {
            user.addEvent(mockk())
        }

        Assert.assertEquals(10, user.events.size)
    }

    @Test
    fun user_getEventReturnsEvent() {
        val eventUUID = UUID.randomUUID()
        val event = Event(mockk<LocalDateTime>(), "test", mockk<File>(), "notes", eventUUID)
        user.addEvent(event)
        val result = user.getEvent(eventUUID)
        Assert.assertEquals(result, event)
    }

    @Test
    fun user_getEventReturnsNullIfNotFound() {
        val event = Event(mockk<LocalDateTime>(), "test", mockk<File>(), "notes", UUID.randomUUID())
        user.addEvent(event)
        val result = user.getEvent(UUID.randomUUID())
        Assert.assertNull(result)
    }
}