package be.hogent.faith.faith.details.drawing.create

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.service.usecases.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.OverwriteDrawingDetailUseCase
import be.hogent.faith.util.factory.DetailFactory
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DrawingDetailViewModelUseCaseTests {

    private lateinit var viewModel: DrawingDetailViewModel

    private lateinit var createDrawingDetailUseCase: CreateDrawingDetailUseCase
    private lateinit var overwriteDrawingDetailUseCase: OverwriteDrawingDetailUseCase

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        createDrawingDetailUseCase = mockk(relaxed = true)
        overwriteDrawingDetailUseCase = mockk(relaxed = true)
        viewModel =
            DrawingDetailViewModel(createDrawingDetailUseCase, overwriteDrawingDetailUseCase)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun drawingDetailVM_existingDetailGiven_noSavedDetailYet() {
        // Arrange
        viewModel.loadExistingDetail(mockk())

        // Assert
        assertNull(getValue(viewModel.savedDetail))
    }

    @Test
    fun drawingDetailVM_noDetailGiven_noSavedDetailYet() {
        // Assert
        assertNull(getValue(viewModel.savedDetail))
    }

    @Test
    fun drawingDetailVM_noDetailGiven_onBitmapAvailable_savesNewDetail() {
        // Arrange
        val bitmap: Bitmap = mockk()
        val createdDetail: DrawingDetail = mockk()
        val detailObserver = mockk<Observer<DrawingDetail>>(relaxed = true)
        val params = slot<CreateDrawingDetailUseCase.Params>()
        val resultingDetailObserver = slot<DisposableSingleObserver<DrawingDetail>>()

        viewModel.savedDetail.observeForever(detailObserver)

        // Act
        viewModel.onBitMapAvailable(bitmap)
        verify {
            createDrawingDetailUseCase.execute(
                capture(params),
                capture(resultingDetailObserver)
            )
        }
        resultingDetailObserver.captured.onSuccess(createdDetail)

        // Assert
        assertEquals(bitmap, params.captured.bitmap)
        assertEquals(createdDetail, getValue(viewModel.savedDetail))
    }

    @Test
    fun drawingDetailVM_detailGiven_onBitmapAvailable_detailStaysSame() {
        // Arrange
        val bitmap: Bitmap = mockk()
        val existingDetail = DetailFactory.makeDrawingDetail()
        val detailObserver = mockk<Observer<DrawingDetail>>(relaxed = true)
        val useCaseParams = slot<OverwriteDrawingDetailUseCase.Params>()
        val completableObserver = slot<DisposableCompletableObserver>()
        viewModel.loadExistingDetail(existingDetail)

        viewModel.savedDetail.observeForever(detailObserver)

        // Act
        viewModel.onBitMapAvailable(bitmap)
        verify {
            overwriteDrawingDetailUseCase.execute(
                capture(useCaseParams),
                capture(completableObserver)
            )
        }
        completableObserver.captured.onComplete()

        // Assert
        assertEquals(bitmap, useCaseParams.captured.bitmap)
        assertEquals(existingDetail, useCaseParams.captured.detail)

        verify { detailObserver.onChanged(existingDetail) }
        val updatedDetail = getValue(viewModel.savedDetail)
        assertEquals(existingDetail, updatedDetail)
    }
}