package be.hogent.faith.service.usecases

import be.hogent.faith.database.storage.IFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.event.GetEventFilesUseCase
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
    private lateinit var fileStorageRepository: IFileStorageRepository
    private lateinit var event: Event

    @Before
    fun setUp() {
        observer = mockk()
        fileStorageRepository = mockk(relaxed = true)
        getEventFilesUseCase = GetEventFilesUseCase(
            fileStorageRepository,
            observer
        )
        event = EventFactory.makeEvent(numberOfDetails = 0)
    }

    @Test
    fun getEventFilesUC_execute_getFiles() {
        every { fileStorageRepository.downloadEventFiles(any()) } returns Completable.complete()
        getEventFilesUseCase.buildUseCaseObservable(
            GetEventFilesUseCase.Params(event)
        )
            .test()
            .assertNoErrors()

        // The image should be stored in the repo
        verify {
            fileStorageRepository.downloadEventFiles(event)
        }
    }

    @Test
    fun getEventFilesUC_execute_failsOnStorageError() {
        every { fileStorageRepository.downloadEventFiles(any()) } returns Completable.error(
            IOException()
        )
        getEventFilesUseCase.buildUseCaseObservable(
            GetEventFilesUseCase.Params(event)
        )
            .test()
            .assertError(IOException::class.java)
    }
}