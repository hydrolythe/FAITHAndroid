package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.util.base64encodeImage
import be.hogent.faith.service.util.getThumbnail
import be.hogent.faith.util.factory.DataFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.Executor

class CreateDrawingDetailUseCaseTest {

    private lateinit var createDrawingDetailUseCase: CreateDrawingDetailUseCase
    private lateinit var executor: Executor
    private lateinit var scheduler: Scheduler
    private lateinit var storageRepository: ITemporaryFileStorageRepository

    private val bitmap = mockk<Bitmap>()

    @Before
    fun setUp() {
        executor = mockk()
        scheduler = mockk()
        storageRepository = mockk(relaxed = true)
        createDrawingDetailUseCase =
            CreateDrawingDetailUseCase(
                storageRepository,
                scheduler
            )
        mockkStatic("be.hogent.faith.service.util.FileConversionKt")
    }

    @Test
    fun createDrawingDetailUC_normal_createsDetailWithCorrectFile() {
        // Arrange
        val saveFile = File("location")
        val thumbnailBitmap = mockk<Bitmap>()
        val thumbnail = DataFactory.randomString()
        every { storageRepository.storeBitmap(any()) } returns Single.just(saveFile)
        every { thumbnailBitmap.base64encodeImage() } returns thumbnail
        every { bitmap.getThumbnail() } returns thumbnailBitmap

        val params = CreateDrawingDetailUseCase.Params(bitmap)

        // Act
        val result = createDrawingDetailUseCase.buildUseCaseSingle(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertValue { newDetail ->
                newDetail.file.path == saveFile.path
            }
            .assertValue { newDetail ->
                newDetail.thumbnail == thumbnail
            }
    }
}