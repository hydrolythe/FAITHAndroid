package be.hogent.faith.faith.details.drawing.create

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.service.usecases.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.OverwriteDrawingDetailUseCase
import be.hogent.faith.util.factory.DetailFactory
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DrawingDetailViewModelTest {

    private lateinit var viewModel: DrawingDetailViewModel

    private lateinit var createDrawingDetailUseCase: CreateDrawingDetailUseCase
    private lateinit var overwriteDrawingDetailUseCase: OverwriteDrawingDetailUseCase

    private val drawingDetail: DrawingDetail = mockk()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        createDrawingDetailUseCase = mockk(relaxed = true)
        overwriteDrawingDetailUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun drawingDetailVM_noDetailGiven_constructs() {
        // Act
        viewModel =
            DrawingDetailViewModel(null, createDrawingDetailUseCase, overwriteDrawingDetailUseCase)

        // Assert
        assertNull(getValue(viewModel.savedDetail))
    }

    @Test
    fun drawingDetailVM_noDetailGiven_onBitmapAvailable_savesDetail() {
        // Arrange
        val bitmap: Bitmap = mockk()
        viewModel =
            DrawingDetailViewModel(null, createDrawingDetailUseCase, overwriteDrawingDetailUseCase)
        val detailObserver = mockk<Observer<DrawingDetail>>(relaxed = true)
        val resultingDetailObserver = slot<DisposableSingleObserver<DrawingDetail>>()

        viewModel.savedDetail.observeForever(detailObserver)

        // Act
        viewModel.onBitMapAvailable(bitmap)
        verify { createDrawingDetailUseCase.execute(any(), capture(resultingDetailObserver)) }
        resultingDetailObserver.captured.onSuccess(drawingDetail)

        // Assert
        verify { detailObserver.onChanged(drawingDetail) }
        assertNotNull(getValue(viewModel.savedDetail))
    }

    @Test
    fun drawingDetailVM_detailGiven_onBitmapAvailable_detailStaysSame() {
        // Arrange
        val bitmap: Bitmap = mockk()
        val drawingDetail = DetailFactory.makeDrawingDetail()
        viewModel =
            DrawingDetailViewModel(
                drawingDetail,
                createDrawingDetailUseCase,
                overwriteDrawingDetailUseCase
            )
        val detailObserver = mockk<Observer<DrawingDetail>>(relaxed = true)
        val completableObserver = slot<DisposableCompletableObserver>()

        viewModel.savedDetail.observeForever(detailObserver)

        // Act
        viewModel.onBitMapAvailable(bitmap)
        verify { overwriteDrawingDetailUseCase.execute(any(), capture(completableObserver)) }
        completableObserver.captured.onComplete()

        // Assert
        verify { detailObserver.onChanged(drawingDetail) }
        val updatedDetail = getValue(viewModel.savedDetail)
        assertEquals(drawingDetail, updatedDetail)
    }
}