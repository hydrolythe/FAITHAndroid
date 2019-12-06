package be.hogent.faith.domain

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class UserTest {
    private lateinit var user: User
    private val eventTitle = "Ingevulde titel"
    private val eventUuid = UUID.randomUUID()
    private lateinit var event: Event

    @Before
    fun setUp() {
        user = User("username", "avatarName", UUID.randomUUID().toString())
//        user = User("username", "R.drawable.avatar")
        event = Event(title = eventTitle, uuid = eventUuid)
    }

    @Test
    fun user_addsEvent() {
        user.addEvent(event)

        Assert.assertEquals(1, user.events.size)
        Assert.assertEquals(event, user.events.first())
    }

    @Test
    fun user_returnsAllDetails() {
        for (i in 1..10) {
            val event = Event(title = "Event $i")
            user.addEvent(event)
        }

        Assert.assertEquals(10, user.events.size)
    }

    @Test
    fun user_getEventReturnsEvent() {
        user.addEvent(event)
        val result = user.getEvent(eventUuid)
        Assert.assertEquals(result, event)
    }

    @Test
    fun user_getEventReturnsNullIfNotFound() {
        user.addEvent(event)
        val result = user.getEvent(UUID.randomUUID())
        Assert.assertNull(result)
    }

    @Test
    fun user_clearEventsclearsAllEvents() {
        user.addEvent(event)
        user.clearEvents()
        Assert.assertEquals(0, user.events.count())
    }
}