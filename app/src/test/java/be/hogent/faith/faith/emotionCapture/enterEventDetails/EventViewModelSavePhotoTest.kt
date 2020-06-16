package be.hogent.faith.faith.emotionCapture.enterEventDetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.event.SaveEventDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.rxjava3.observer.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventViewModelSavePhotoTest {
    private lateinit var viewModel: EventViewModel
    private val saveEventPhotoUC = mockk<SaveEventDetailUseCase>(relaxed = true)

    private val detail = mockk<PhotoDetail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private val event
        get() = viewModel.event.value!!

    @Before
    fun setUp() {
        viewModel = EventViewModel(
            mockk(),
            saveEventPhotoUC,
            mockk()
        )
    }

    @Test
    fun eventViewModel_saveEventPhoto_callsUseCase() {
        val params = slot<SaveEventDetailUseCase.Params>()

        viewModel.savePhotoDetail(detail)
        verify { saveEventPhotoUC.execute(capture(params), any()) }

        assertEquals(detail, params.captured.detail)
        assertEquals(event, params.captured.event)
    }

    @Test
    fun eventViewModel_saveEventPhoto_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.photoDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.savePhotoDetail(detail)
        verify { saveEventPhotoUC.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_photo_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun eventViewModel_saveEventPhoto_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()

        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.photoDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.savePhotoDetail(detail)
        verify { saveEventPhotoUC.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}