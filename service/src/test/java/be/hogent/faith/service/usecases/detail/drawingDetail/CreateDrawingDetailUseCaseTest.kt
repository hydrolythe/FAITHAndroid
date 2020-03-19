package be.hogent.faith.service.usecases.detail.drawingDetail

import android.graphics.Bitmap
import be.hogent.faith.storage.localStorage.ITemporaryStorageRepository
import io.mockk.every
import io.mockk.mockk
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
    private lateinit var storageRepository: ITemporaryStorageRepository

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
    }

    @Test
    fun createDrawingDetailUC_normal_createsDetailWithCorrectFile() {
        // Arrange
        val saveFile = File("location")
        every { storageRepository.storeBitmapTemporarily(any()) } returns Single.just(saveFile)

        val params = CreateDrawingDetailUseCase.Params(bitmap)

        // Act
        val result = createDrawingDetailUseCase.buildUseCaseSingle(params)

        // Assert
        result.test()
            .assertNoErrors()
            .assertValue { newDetail ->
                newDetail.file.path == saveFile.path
            }
    }
}