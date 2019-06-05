package be.hogent.faith.faith.emotionCapture.recordAudio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecordAudioViewModelTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecordAudioViewModel

    @Before
    fun setUp() {
        viewModel = RecordAudioViewModel()
    }

    @Test
    fun recordAudioVM_initialisation_correctState() {
        assertEquals(RecordAudioViewModel.RecordingStatus.INITIAL, viewModel.recordState.value)
    }

    @Test
    fun recordAudioVM_recordButtonClicked_correctState() {
        // Act
        viewModel.onRecordButtonClicked()

        // Assert
        assertEquals(RecordAudioViewModel.RecordingStatus.RECORDING, viewModel.recordState.value)
    }

    @Test
    fun recordAudioVM_pauseButtonClicked_correctState() {
        // Act
        viewModel.onPauseButtonClicked()

        // Assert
        assertEquals(RecordAudioViewModel.RecordingStatus.PAUSED, viewModel.recordState.value)
    }

    @Test
    fun recordAudioVM_stopButtonClicked_correctState() {
        // Act
        viewModel.onStopButtonClicked()

        // Assert
        assertEquals(RecordAudioViewModel.RecordingStatus.STOPPED, viewModel.recordState.value)
    }
}