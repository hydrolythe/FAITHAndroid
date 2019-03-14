package be.hogent.faith.faith.recordAudio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.SaveAudioRecordingUseCase
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
import java.io.IOException

class RecordAudioViewModelTest {
    private lateinit var viewModel: RecordAudioViewModel


    private val saveAudioRecordingUseCase = mockk<SaveAudioRecordingUseCase>()
    private val tempRecordingFile = mockk<File>()
    private val event = mockk<Event>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = RecordAudioViewModel(saveAudioRecordingUseCase, tempRecordingFile, event)
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
        val params = slot<SaveAudioRecordingUseCase.SaveAudioRecordingParams>()
        every { saveAudioRecordingUseCase.execute(capture(params)) } returns Completable.complete()
        // Act
        viewModel.onSaveButtonClicked()

        verify { saveAudioRecordingUseCase.execute(any()) }

        assertEquals(event, params.captured.event)
        assertEquals(tempRecordingFile, params.captured.tempStorageFile)
    }

    @Test
    fun recordAudioVM_saveButtonClicked_notifiesWhenSaveCompletes() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        every { saveAudioRecordingUseCase.execute(any()) } returns Completable.complete()
        viewModel.recordingSavedSuccessFully.observeForever(successObserver)
        viewModel.recordingSaveFailed.observeForever(failedObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { successObserver.onChanged(any()) }
        verify { failedObserver wasNot Called }
    }

    @Test
    fun recordAudioVM_saveButtonClicked_notifiesWithErrorMessageWhenSaveFails() {
        // Arrange
        val successObserver = mockk<Observer<Unit>>(relaxed = true)
        val failedObserver = mockk<Observer<String>>(relaxed = true)
        val errorMessage = "Something failed"

        every { saveAudioRecordingUseCase.execute(any()) } returns Completable.error(RuntimeException(errorMessage))

        viewModel.recordingSavedSuccessFully.observeForever(successObserver)
        viewModel.recordingSaveFailed.observeForever(failedObserver)

        // Act
        viewModel.onSaveButtonClicked()

        // Assert
        verify { failedObserver.onChanged(errorMessage) }
        verify { successObserver wasNot Called }
    }
}