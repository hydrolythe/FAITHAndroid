package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventViewModelSaveTextTest {
    private lateinit var viewModel: EventViewModel
    private val saveTextUseCase = mockk<SaveEventDetailUseCase>(relaxed = true)
    private val detail = mockk<TextDetail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = EventViewModel(
            mockk(),
            saveTextUseCase,
            mockk()
        )
    }

    @Test
    fun eventViewModel_saveText_callsUseCase() {
        val params = slot<SaveEventDetailUseCase.Params>()

        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
        assertEquals(viewModel.event.value, params.captured.event)
    }

    @Test
    fun eventViewModel_saveText_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_text_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun eventViewModel_saveText_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.textDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.saveTextDetail(detail)
        verify { saveTextUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}