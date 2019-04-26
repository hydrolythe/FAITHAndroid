package be.hogent.faith.service.usecases

import be.hogent.faith.database.factory.UserFactory
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
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
        val params = GetEventsUseCase.Params(UserFactory.makeUser())

        getEventsUC.buildUseCaseObservable(params)

        verify { repository.getAll(any()) }
    }

    @Test
    fun getEventsUseCase_userIsPassedToRepo() {
        val userArg = slot<User>()
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        every { repository.getAll(capture(userArg)) } returns Flowable.just(EventFactory.makeEventList())

        getEventsUC.buildUseCaseObservable(params)

        assertEquals(params.user, userArg.captured)
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        // Simulate two events on the stream
        every { repository.getAll(any()) } returns Flowable.just(
            EventFactory.makeEventList(2),
            EventFactory.makeEventList(2)
        )
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertValueCount(2)
    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        every { repository.getAll(any()) } returns Flowable.empty()
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertNoValues()
    }
}
