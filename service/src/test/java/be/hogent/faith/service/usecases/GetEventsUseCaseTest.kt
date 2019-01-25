package be.hogent.faith.service.usecases

import be.hogent.faith.domain.repository.EventRepository
import io.mockk.mockk
import io.mockk.verify
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
        repository = mockk()
        getEventsUC = GetEventsUseCase(repository, scheduler)
    }

    @Test
    fun getEventsUC_execute_callsRepo() {
        getEventsUC.execute(mockk())
        verify { repository.getAll() }
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {

    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {

    }


}
