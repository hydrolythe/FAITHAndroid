package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
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
        val userArg = slot<User>()
        val user = UserFactory.makeUser()
        every { repository.insert(capture(eventArg), capture(userArg)) } returns Completable.complete()

        val dateTime = DataFactory.randomDateTime()
        val title = DataFactory.randomString()
        val params = CreateEventUseCase.Params(dateTime, title, user)

        // Act
        createEventUseCase.buildUseCaseObservable(params)

        // Assert
        assertEquals(params.title, eventArg.captured.title)
        assertEquals(params.dateTime, eventArg.captured.dateTime)
        assertEquals(params.user, userArg.captured)
    }
}
