package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.event.GetEventFilesUseCase
import be.hogent.faith.storage.IStorageRepository
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test
import java.io.IOException

class GetEventFilesUseCaseTest {
    private lateinit var getEventFilesUseCase: GetEventFilesUseCase
    private lateinit var observer: Scheduler
    private lateinit var storageRepository: IStorageRepository
    private lateinit var event: Event

    @Before
    fun setUp() {
        observer = mockk()
        storageRepository = mockk(relaxed = true)
        getEventFilesUseCase = GetEventFilesUseCase(
            storageRepository,
            observer
        )
        event = EventFactory.makeEvent(nbrOfDetails = 0)
    }

    @Test
    fun getEventFilesUC_execute_getFiles() {
        every { storageRepository.getEvent(any()) } returns Completable.complete()
        getEventFilesUseCase.buildUseCaseObservable(
            GetEventFilesUseCase.Params(event)
        )
            .test()
            .assertNoErrors()

        // The image should be stored in the repo
        verify {
            storageRepository.getEvent(event)
        }
    }

    @Test
    fun getEventFilesUC_execute_failsOnStorageError() {
        every { storageRepository.getEvent(any()) } returns Completable.error(
            IOException()
        )
        getEventFilesUseCase.buildUseCaseObservable(
            GetEventFilesUseCase.Params(event)
        )
            .test()
            .assertError(IOException::class.java)
    }
}