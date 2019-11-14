package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.event.SaveEventDrawingDetailUseCase
import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SaveEventDrawingDetailUseCaseTest {

    private lateinit var saveEventDrawingUC: SaveEventDrawingDetailUseCase
    private val scheduler = mockk<Scheduler>()
    private lateinit var storageRepository: StorageRepository

    private val drawingDetail = mockk<DrawingDetail>()
    private val event = Event()

    @Before
    fun setUp() {
        storageRepository = mockk(relaxed = true)
        saveEventDrawingUC = SaveEventDrawingDetailUseCase(
            storageRepository,
            scheduler
        )
    }

    @Test
    fun saveEventDrawingUC_newDetail_savesToEvent() {
        // Arrange
        val params = SaveEventDrawingDetailUseCase.Params(drawingDetail, event)
        every {
            storageRepository.storeDrawingDetailWithEvent(drawingDetail, event)
        } returns Completable.complete()

        // Act
        val result = saveEventDrawingUC.buildUseCaseObservable(params)

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
        event.addDetail(drawingDetail)

        val params = SaveEventDrawingDetailUseCase.Params(drawingDetail, event)
        every {
            storageRepository.storeDrawingDetailWithEvent(drawingDetail, event)
        } returns Completable.complete()

        // Act
        val result = saveEventDrawingUC.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertComplete()

        assertEquals(1, event.details.size)
        assertEquals(drawingDetail, event.details.first())
    }
}