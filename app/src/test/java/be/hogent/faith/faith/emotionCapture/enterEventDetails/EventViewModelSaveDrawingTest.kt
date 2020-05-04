package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventViewModelSaveDrawingTest {
    private lateinit var viewModel: EventViewModel
    private val saveEventDrawingUseCase = mockk<SaveEventDetailUseCase>(relaxed = true)

    private val drawingDetail = mockk<DrawingDetail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventViewModel(
            mockk(),
            saveEventDrawingUseCase,
            mockk()
        )
    }

    @Test
    fun eventVM_saveDrawing_NotifyOnSuccess() {
        // Arrange
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val useCaseObserver = slot<DisposableCompletableObserver>()
        viewModel.drawingDetailSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(errorObserver)
        every { saveEventDrawingUseCase.execute(any(), capture(useCaseObserver)) } returns Unit

        // Act
        viewModel.saveDrawingDetail(drawingDetail)
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_drawing_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun eventVM_saveDrawing_NotifyOnFailure() {
        // Arrange
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val useCaseObserver = slot<DisposableCompletableObserver>()
        viewModel.drawingDetailSavedSuccessFully.observeForever(successObserver)
        viewModel.errorMessage.observeForever(errorObserver)
        every { saveEventDrawingUseCase.execute(any(), capture(useCaseObserver)) } returns Unit

        // Act
        viewModel.saveDrawingDetail(drawingDetail)
        useCaseObserver.captured.onError(mockk())

        // Assert
        verify { errorObserver.onChanged(R.string.error_save_drawing_failed) }
        verify { successObserver wasNot called }
    }
}