package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.event.MakeEventFilesAvailableUseCase
import be.hogent.faith.util.factory.EventFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test
import java.io.IOException

class MakeEventFilesAvailableUseCaseTest {
    private lateinit var makeEventFilesAvailableUseCase: MakeEventFilesAvailableUseCase
    private val observer: Scheduler = mockk()
    private val fileStorageRepository: IFileStorageRepository = mockk(relaxed = true)
    private val eventRepository: IEventRepository = mockk(relaxed = true)
    private val evenEncryptionService: IEventEncryptionService = mockk(relaxed = true)
    private lateinit var event: Event

    @Before
    fun setUp() {
        makeEventFilesAvailableUseCase = MakeEventFilesAvailableUseCase(
            fileStorageRepository,
            eventRepository,
            evenEncryptionService,
            observer
        )
        event = EventFactory.makeEvent(numberOfDetails = 0)
    }

    @Test
    fun getEventFilesUC_execute_getFiles() {
        every { fileStorageRepository.downloadEventFiles(any()) } returns Completable.complete()
        makeEventFilesAvailableUseCase.buildUseCaseObservable(
            MakeEventFilesAvailableUseCase.Params(event)
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
        makeEventFilesAvailableUseCase.buildUseCaseObservable(
            MakeEventFilesAvailableUseCase.Params(event)
        )
            .test()
            .assertError(IOException::class.java)
    }
}