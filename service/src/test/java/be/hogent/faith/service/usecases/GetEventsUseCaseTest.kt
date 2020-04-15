package be.hogent.faith.service.usecases

import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import be.hogent.faith.service.usecases.util.EncryptedEventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class GetEventsUseCaseTest {
    private lateinit var getEventsUC: GetEventsUseCase
    private val scheduler: Scheduler = mockk()
    private val eventEncryptionService: IEventEncryptionService = mockk()
    private val repository: IEventRepository = mockk()

    @Before
    fun setUp() {
        getEventsUC = GetEventsUseCase(repository, eventEncryptionService, scheduler)
    }

    @Test
    fun getEventsUC_execute_callsRepo() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())

        getEventsUC.buildUseCaseObservable(params)

        verify { repository.getAll() }
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        // Simulate two events on the stream
        every { repository.getAll() } returns Flowable.just(
            EncryptedEventFactory.makeEventList(2),
            EncryptedEventFactory.makeEventList(2)
        )
        getEventsUC.buildUseCaseObservable(params)
            .test()
            .assertValueCount(2)
    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        every { repository.getAll() } returns Flowable.empty()
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertNoValues()
    }
}
