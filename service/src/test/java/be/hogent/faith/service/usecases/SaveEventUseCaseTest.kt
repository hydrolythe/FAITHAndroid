package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.EventFactory
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class SaveEventUseCaseTest {
    private lateinit var saveEventUseCase: SaveEventUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: EventRepository

    private lateinit var event: Event
    private lateinit var user: User

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        event = EventFactory.makeEvent(nbrOfDetails = 0)
        user = spyk(User(DataFactory.randomString(), DataFactory.randomString()))
        repository = mockk(relaxed = true)
        saveEventUseCase = SaveEventUseCase(repository, scheduler)
    }

    @Test
    fun execute_normal_eventCorrectlyPassedToRepo() {
        // Arrange
        val eventArg = slot<Event>()
        val userArg = slot<User>()
        every { repository.insert(capture(eventArg), capture(userArg)) } returns Completable.complete()

        val params = SaveEventUseCase.Params(event, user)

        // Act
        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .dispose()

        // Assert
        assertEquals(params.event, eventArg.captured)
        assertEquals(params.user, userArg.captured)
    }

    @Test
    fun execute_normal_useCaseCompletes() {
        // Arrange
        every { repository.insert(any(), any()) } returns Completable.complete()

        val params = SaveEventUseCase.Params(event, user)

        // Act
        val result = saveEventUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertComplete()
            .dispose()
    }

    @Test
    fun execute_normal_eventIsAddedToUser() {
        // Arrange
        every { repository.insert(any(), any()) } returns Completable.complete()

        val params = SaveEventUseCase.Params(event, user)

        // Act
        val result = saveEventUseCase.buildUseCaseObservable(params)
        result.test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        assertTrue(user.events.isNotEmpty())
    }

    @Test
    fun execute_repoFails_showsError() {
        // Arrange
        every { repository.insert(any(), any()) } returns Completable.error(RuntimeException())

        val params = SaveEventUseCase.Params(event, user)

        // Act
        val result = saveEventUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertError(RuntimeException::class.java)
            .assertNoValues()
            .dispose()
    }

    @Test
    fun execute_addEventToUserFails_notSavedToRepo() {
        // Arrange
        every { user.addEvent(any()) } throws java.lang.RuntimeException()

        val params = SaveEventUseCase.Params(event, user)

        // Act
        val result = saveEventUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertError(RuntimeException::class.java)
            .assertNoValues()
            .dispose()
        verify { repository.insert(any(), any()) wasNot called }
    }
}