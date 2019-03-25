package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
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
import java.util.UUID
import java.util.concurrent.Executor

class SaveEventUseCaseTest {
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
    fun execute_eventIsPassedToRepo() {
        // Arrange
        val eventArg = slot<Event>()
        val userArg = slot<User>()
        val user = User(UUID.randomUUID())
        every { repository.insert(capture(eventArg), capture(userArg)) } returns Completable.complete()

        val dateTime = LocalDateTime.of(2018, 10, 28, 8, 22)
        val params = CreateEventUseCase.Params(dateTime, "title", user)

        // Act
        val result = createEventUseCase.buildUseCaseObservable(params)

        // Assert
        assertEquals(params.title, eventArg.captured.title)
        assertEquals(params.dateTime, eventArg.captured.dateTime)
        assertEquals(params.user, userArg.captured)
    }
}