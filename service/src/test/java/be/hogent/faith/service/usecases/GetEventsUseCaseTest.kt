package be.hogent.faith.service.usecases

import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import be.hogent.faith.service.usecases.util.EncryptedEventFactory
import be.hogent.faith.util.factory.EventFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class GetEventsUseCaseTest {
    private lateinit var getEventsUC: GetEventsUseCase
    private val eventEncryptionService = mockk<IEventEncryptionService>()
    private val eventRepository = mockk<IEventRepository>()

    @Before
    fun setUp() {
        getEventsUC = GetEventsUseCase(
            eventRepository,
            eventEncryptionService,
            mockk(),
            Schedulers.trampoline()
        )
    }

    @Test
    fun getEventsUC_execute_callsRepo() {
        every { eventRepository.getAll() } returns Flowable.just(
            EncryptedEventFactory.makeEventList(2),
            EncryptedEventFactory.makeEventList(2)
        )
        val params = GetEventsUseCase.Params(UserFactory.makeUser())

        getEventsUC.buildUseCaseObservable(params)
            .test()

        verify { eventRepository.getAll() }
    }

    @Test
    fun getEventsUseCase_eventsPresent_returnsThem() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        // Simulate two events on the stream
        every { eventRepository.getAll() } returns Flowable.just(
            EncryptedEventFactory.makeEventList(2),
            EncryptedEventFactory.makeEventList(2)
        )
        every { eventEncryptionService.decryptData(any()) } returns Single.defer { // Defer to ensure a new event is made with each call
            Single.just(EventFactory.makeEvent())
        }
        getEventsUC.buildUseCaseObservable(params)
            .test()
            .assertValueCount(2)
    }

    @Test
    fun getEventsUseCase_noEventsPresent_returnsNothing() {
        val params = GetEventsUseCase.Params(UserFactory.makeUser())
        every { eventRepository.getAll() } returns Flowable.empty()
        val result = getEventsUC.buildUseCaseObservable(params)
        result.test().assertNoValues()
    }
}
