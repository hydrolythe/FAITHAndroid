package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.EventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test
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
        getEventsUC.buildUseCaseObservable(mockk())
        verify { repository.getAll() }
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {
        // Simulate two events on the stream
        every { repository.getAll() } returns Flowable.just(
            listOf(createEvent(), createEvent()),
            listOf(createEvent(), createEvent()))
        val result = getEventsUC.buildUseCaseObservable(mockk())
        result.test().assertValueCount(2)
    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {
        every { repository.getAll() } returns Flowable.empty()
        val result = getEventsUC.buildUseCaseObservable(mockk())
        result.test().assertNoValues()
    }

    private fun createEvent(): Event {
        return Event(mockk(), "title", mockk())
    }
}
