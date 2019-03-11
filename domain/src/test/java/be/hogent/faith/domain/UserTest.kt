package be.hogent.faith.domain

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UserTest {
    private lateinit var user: User

    @Before
    fun setUp() {
        user = User(mockk())
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
}