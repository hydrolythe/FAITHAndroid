package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.fakes.SingleUserFakeEventRepository
import be.hogent.faith.service.usecases.event.SaveEventUseCase
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveEventUseCaseTest {
    private lateinit var saveEventUseCase: SaveEventUseCase
    private lateinit var eventRepository: EventRepository

    private lateinit var event: Event
    private lateinit var user: User

    private val eventTitle = "title"

    @Before
    fun setUp() {
        event = EventFactory.makeEvent(numberOfDetails = 2, hasEmotionAvatar = true)
        user = UserFactory.makeUser(numberOfEvents = 0)
        eventRepository = SingleUserFakeEventRepository()
        saveEventUseCase =
            SaveEventUseCase(eventRepository, mockk(relaxed = true))
    }

    @Test
    fun `After saving the event it should be available in the EventRepository`() {
        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .dispose()

        val foundEvent = eventRepository.get(event.uuid).test().values().first()
        assertEquals(event, foundEvent)
    }

    @Test
    fun `After saving the event it should be in the user's list of events`() {
        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)

        result.test()
            .dispose()

        assertTrue(user.events.contains(event))
    }

    @Test
    fun `When an error occurs in the EventRepository it returns an Error`() {
        eventRepository = mockk()
        every { eventRepository.insert(any(), any()) } throws RuntimeException()
        saveEventUseCase =
            SaveEventUseCase(eventRepository, mockk(relaxed = true))

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .assertError(RuntimeException::class.java)
    }

    @Test
    fun `When an error occurs in the EventRepository the event is not added to the user's events`() {
        eventRepository = mockk()
        every { eventRepository.insert(any(), any()) } throws RuntimeException()
        saveEventUseCase =
            SaveEventUseCase(eventRepository, mockk(relaxed = true))

        val params = SaveEventUseCase.Params(eventTitle, event, user)

        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()

        assertFalse(user.events.contains(event))
    }
}