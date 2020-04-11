package be.hogent.faith.database.authentication

import be.hogent.faith.database.event.EventDatabase
import be.hogent.faith.database.event.EventMapper
import be.hogent.faith.database.event.EventRepository
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.After
import org.junit.Before

class EventRepositoryTest {

    private val eventDatabase = mockk<EventDatabase>()

    private val eventRepository = EventRepository(eventDatabase)

    private val user = UserFactory.makeUser(numberOfEvents = 0)
    private val event = EventFactory.makeEvent(numberOfDetails = 2)

    @Before
    fun setUpMocks() {
        mockkObject(EventMapper)
        mockkObject(UserMapper)
    }

    @After
    fun clearMocks() {
        clearAllMocks()
    }
    // TODO: write tests
}