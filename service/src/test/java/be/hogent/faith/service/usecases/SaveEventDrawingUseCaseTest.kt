package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class SaveEventDrawingUseCaseTest {

    private lateinit var useCase: SaveEventDrawingUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var storageRepository: StorageRepository

    private val drawingDetail = mockk<DrawingDetail>()

    private lateinit var event: Event

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        storageRepository = mockk(relaxed = true)
        useCase = SaveEventDrawingUseCase(storageRepository, scheduler)
    }

    @Test
    fun saveEventDrawingUC_newDetail_savesToEvent() {
        // Arrange
        event = Event()

        val params = SaveEventDrawingUseCase.Params(drawingDetail, event)
        every {
            storageRepository.saveEventDrawing(params.detail, params.event)
        } returns Completable.complete()

        // Act
        val result = useCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        assertEquals(1, event.details.size)
        assertEquals(drawingDetail, event.details.first())
    }

    @Test
    fun saveEventDrawingUC_existingDetail_noNewDetailAdded() {
        // Arrange
        event = Event()
        event.addDetail(drawingDetail)

        val params = SaveEventDrawingUseCase.Params(drawingDetail, event)
        every {
            storageRepository.saveEventDrawing(params.detail, params.event)
        } returns Completable.complete()

        // Act
        val result = useCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        assertEquals(1, event.details.size)
        assertEquals(drawingDetail, event.details.first())
    }
}