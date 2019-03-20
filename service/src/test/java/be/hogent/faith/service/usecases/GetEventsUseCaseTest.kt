package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.Executor

class GetEventsUseCaseTest {
    private lateinit var getEventsUC: GetEventsUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: EventRepository

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        repository = mockk(relaxed = true)
        getEventsUC = GetEventsUseCase(repository, scheduler)
    }

    @Test
    fun getEventsUC_execute_callsRepo() {
        val params = GetEventsUseCase.Params(createUser())
        getEventsUC.buildUseCaseObservable(params)
        verify { repository.getAll(any()) }
    }

    @Test
    fun getEventsUseCase_userIsPassedToRepo() {
        val userArg = slot<User>()
        val params = GetEventsUseCase.Params(createUser())
        every { repository.getAll(capture(userArg)) } returns Flowable
            .just(listOf(createEvent()))
        getEventsUC.buildUseCaseObservable(params)
        assertEquals(params.user, userArg.captured)
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {
        val params = GetEventsUseCase.Params(createUser())
        // Simulate two events on the stream
        every { repository.getAll(any()) } returns Flowable.just(
            listOf(createEvent(), createEvent()),
            listOf(createEvent(), createEvent()))
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertValueCount(2)
    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {
        val params = GetEventsUseCase.Params(createUser())
        every { repository.getAll(any()) } returns Flowable.empty()
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertNoValues()
    }

    private fun createEvent(): Event {
        return Event(mockk(), "title", mockk())
    }

    private fun createUser(): User {
        return User("username", "avatar", UUID.randomUUID())
    }
}
