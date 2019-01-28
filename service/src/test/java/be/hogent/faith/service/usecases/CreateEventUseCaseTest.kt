package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.EventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.util.concurrent.Executor

class CreateEventUseCaseTest {
    private lateinit var createEventUseCase: CreateEventUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: EventRepository

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        repository = mockk(relaxed = true)
        createEventUseCase = CreateEventUseCase(repository, scheduler)


    }

    @Test
    fun createEventUC_eventIsPassedToRepo() {
        // Arrange
        val eventArg = slot<Event>()
        every { repository.insert(capture(eventArg)) } returns Completable.complete()
        val dateTime = LocalDateTime.of(2018, 10, 28, 8,22)
        val params = CreateEventUseCase.CreateEventParameters(dateTime, "description")

        // Act
        val result = createEventUseCase.buildUseCaseObservable(params)

        // Assert
        assertEquals(params.description, eventArg.captured.description)
        assertEquals(params.dateTime, eventArg.captured.dateTime)
    }
}
