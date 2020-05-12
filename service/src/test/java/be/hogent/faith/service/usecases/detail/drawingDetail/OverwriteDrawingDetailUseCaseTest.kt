package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.util.factory.DataFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.Executor

class OverwriteDrawingDetailUseCaseTest {

    private lateinit var overwriteDrawingDetailUseCase: OverwriteDrawingDetailUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var storageRepository: ITemporaryFileStorageRepository

    private val bitmap = mockk<Bitmap>()

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        storageRepository = mockk(relaxed = true)
        overwriteDrawingDetailUseCase =
            OverwriteDrawingDetailUseCase(
                storageRepository,
                scheduler
            )
    }

    @Test
    fun overwriteDrawingDetailUC_normal_detailSaveFileStaysSame() {
        // Arrange
        val saveFile = File("location")
        val thumbnail = DataFactory.randomString()
        val drawingDetail = DrawingDetail(saveFile, thumbnail)
        every {
            storageRepository.overwriteExistingDrawingDetail(
                bitmap,
                drawingDetail
            )
        } returns Completable.complete()

        val params = OverwriteDrawingDetailUseCase.Params(bitmap, drawingDetail)

        // Act
        val result = overwriteDrawingDetailUseCase.buildUseCaseObservable(params)

        // Assert
        result.test()
            .assertNoErrors()
        verify { storageRepository.overwriteExistingDrawingDetail(bitmap, drawingDetail) }
        assertEquals(saveFile, drawingDetail.file)
        assertEquals(thumbnail, drawingDetail.thumbnail)
    }
}
