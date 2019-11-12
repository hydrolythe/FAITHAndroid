package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.drawingDetail.SaveEventDrawingDetailUseCase
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor

class SaveEventDrawingDetailUseCaseTest {

    private lateinit var detailUseCase: SaveEventDrawingDetailUseCase
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
        detailUseCase = SaveEventDrawingDetailUseCase(
            storageRepository,
            scheduler
        )
    }

    @Test
    fun saveEventDrawingUC_newDetail_savesToEvent() {
        // Arrange
        event = Event()

        val params = SaveEventDrawingDetailUseCase.Params(drawingDetail, event)
        every {
            storageRepository.saveEventDrawing(params.detail, params.event)
        } returns Completable.complete()

        // Act
        val result = detailUseCase.buildUseCaseObservable(params)

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

        val params = SaveEventDrawingDetailUseCase.Params(drawingDetail, event)
        every {
            storageRepository.saveEventDrawing(params.detail, params.event)
        } returns Completable.complete()

        // Act
        val result = detailUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        assertEquals(1, event.details.size)
        assertEquals(drawingDetail, event.details.first())
    }
}