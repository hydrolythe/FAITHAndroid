package be.hogent.faith.faith.emotionCapture.recordAudio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.SaveAudioRecordingUseCase
import io.mockk.Called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecordAudioViewModelTest {
    private lateinit var viewModel: RecordAudioViewModel

    private val saveAudioRecordingUseCase = mockk<SaveAudioRecordingUseCase>(relaxed = true)
    private val event = mockk<Event>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = RecordAudioViewModel(saveAudioRecordingUseCase, event)
    }

    @Test
    fun recordAudioVM_initialisation_correctState() {
        assertEquals(RecordAudioViewModel.RecordingStatus.INITIAL, viewModel.recordingStatus.value)
    }

    @Test
    fun recordAudioVM_recordButtonClicked_correctState() {
        // Act
        viewModel.onRecordButtonClicked()

        // Assert
        assertEquals(RecordAudioViewModel.RecordingStatus.RECORDING, viewModel.recordingStatus.value)
    }

    @Test
    fun recordAudioVM_pauseButtonClicked_correctState() {
        // Act
        viewModel.onPauseButtonClicked()

        // Assert
        assertEquals(RecordAudioViewModel.RecordingStatus.PAUSED, viewModel.recordingStatus.value)
    }

    @Test
    fun recordAudioVM_stopButtonClicked_correctState() {
        // Act
        viewModel.onStopButtonClicked()

        // Assert
        assertEquals(RecordAudioViewModel.RecordingStatus.STOPPED, viewModel.recordingStatus.value)
    }

    @Test
    fun recordAudioVM_saveButtonClicked_callsUseCase() {
        // Arrange
        val params = slot<SaveAudioRecordingUseCase.Params>()
        val observer = slot<DisposableCompletableObserver>()
        viewModel.tempRecordingFile = mockk()

        // Act
        viewModel.onSaveButtonClicked()
        verify { saveAudioRecordingUseCase.execute(capture(params), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        assertEquals(event, params.captured.event)
    }

    @Test
    fun recordAudioVM_saveButtonClicked_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val observer = slot<DisposableCompletableObserver>()

        viewModel.recordingSavedSuccessFully.observeForever(successObserver)
        viewModel.recordingSaveFailed.observeForever(failedObserver)
        viewModel.tempRecordingFile = mockk()

        // Act
        viewModel.onSaveButtonClicked()
        verify { saveAudioRecordingUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onComplete()

        // Assert
        assertEquals("", viewModel.recordingName.value)
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun recordAudioVM_saveButtonClicked_notifiesWithErrorMessageWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val observer = slot<DisposableCompletableObserver>()

        viewModel.tempRecordingFile = mockk()

        viewModel.recordingSavedSuccessFully.observeForever(successObserver)
        viewModel.recordingSaveFailed.observeForever(failedObserver)

        // Act
        viewModel.onSaveButtonClicked()
        verify { saveAudioRecordingUseCase.execute(any(), capture(observer)) }
        // Make the UC-handler call the success handler
        observer.captured.onError(mockk(relaxed = true))

        // Assert
        verify { failedObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}