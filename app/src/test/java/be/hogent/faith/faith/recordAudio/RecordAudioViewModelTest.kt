package be.hogent.faith.faith.recordAudio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecordAudioViewModelTest {
    private lateinit var viewModel: RecordAudioViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = RecordAudioViewModel()
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
}