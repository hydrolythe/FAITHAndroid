package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.IEventRepository
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test
import java.lang.RuntimeException
import java.util.concurrent.Executor

class GetEventsUseCaseTest {
    private lateinit var getEventsUC: GetEventsUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var repository: IEventRepository

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

        verify { repository.getAllEventsData() }
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        // Simulate two events on the stream
        every { repository.getAllEventsData() } returns Observable.just(
            EventFactory.makeEventList(2),
            EventFactory.makeEventList(2)
        )
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertValueCount(2)
    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        every { repository.getAllEventsData() } returns Observable.empty()
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertNoValues()
    }

    @Test
    fun getEventsUseCase_userNotAuthenticated_fails() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        every { repository.getAllEventsData() } returns Observable.error(RuntimeException())
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertNoValues().assertError(RuntimeException::class.java)
    }
}
