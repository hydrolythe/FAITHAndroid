package be.hogent.faith.faith.emotionCapture.takePhoto

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.TakeEventPhotoUseCase
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class TakePhotoViewModelTest {
    private val takeEventPhotoUseCase = mockk<TakeEventPhotoUseCase>()
    private val tempPhotoFile = mockk<File>()
    private val event = mockk<Event>()

    private lateinit var viewModel: TakePhotoViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = TakePhotoViewModel(takeEventPhotoUseCase, event)
    }

    @Test
    fun takePhotoVM_onTakePhotoButtonClicked_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Unit>>()
        every { observer.onChanged(any()) } returns Unit
        viewModel.takePhotoButtonClicked.observeForever(observer)

        // Act
        viewModel.onTakePhotoButtonClicked()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun takePhotoVM_onCancelButtonClicked_callsListeners() {
        // Arrange
        val observer = mockk<Observer<Unit>>(relaxed = true)
        viewModel.cancelButtonClicked.observeForever(observer)

        // Act
        viewModel.onCancelButtonClicked()

        // Assert
        verify { observer.onChanged(any()) }
    }

    @Test
    fun takePhotoVM_saveButtonClicked_callsUseCase() {
        // Arrange
        val params = slot<TakeEventPhotoUseCase.Params>()
        every { takeEventPhotoUseCase.execute(capture(params)) } returns Completable.complete()
        viewModel.tempPhotoFile = mockk()

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { takeEventPhotoUseCase.execute(any()) }
        assertEquals(event, params.captured.event)
    }

    @Test
    fun takePhotoVM_saveButtonClicked_notifiesWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        every { takeEventPhotoUseCase.execute(any()) } returns Completable.complete()
        viewModel.photoSavedSuccessFully.observeForever(successObserver)
        viewModel.recordingSaveFailed.observeForever(failedObserver)
        viewModel.tempPhotoFile = mockk()

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun takePhotoVM_saveButtonClicked_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val errorMessage = "Something failed"
        viewModel.tempPhotoFile = mockk()

        every { takeEventPhotoUseCase.execute(any()) } returns Completable.error(RuntimeException(errorMessage))

        viewModel.photoSavedSuccessFully.observeForever(successObserver)
        viewModel.recordingSaveFailed.observeForever(failedObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { failedObserver.onChanged(errorMessage) }
        verify { successObserver wasNot Called }
    }
}
