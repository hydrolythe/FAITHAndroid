package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SaveEventDrawingDetailUseCaseTest {

    private lateinit var saveEventDrawingUC: SaveEventDetailUseCase
    private val scheduler = mockk<Scheduler>()
    private lateinit var storageRepository: ITemporaryFileStorageRepository

    private val drawingDetail = mockk<DrawingDetail>()
    private val event = Event()

    @Before
    fun setUp() {
        storageRepository = mockk(relaxed = true)
        saveEventDrawingUC =
            SaveEventDetailUseCase(
                storageRepository,
                scheduler
            )
    }

    @Test
    fun saveEventDrawingUC_newDetail_savesToEvent() {
        // Arrange
        val params = SaveEventDetailUseCase.Params(drawingDetail, event)
        every {
            storageRepository.storeDetailWithEvent(drawingDetail, event)
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

        val params = SaveEventDetailUseCase.Params(drawingDetail, event)
        every {
            storageRepository.storeDetailWithEvent(drawingDetail, event)
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