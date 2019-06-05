package be.hogent.faith.faith.emotionCapture.recordAudio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule

class RecordAudioViewModelTest {

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecordAudioViewModel

    @Before
    fun setUp() {
        viewModel = RecordAudioViewModel()
    }

//    @Test
//    fun recordAudioVM_initialisation_correctState() {
//        assertEquals(RecordStateStopped::class, viewModel.recordState.value!!::class)
//    }
//
//    @Test
//    fun recordAudioVM_recordButtonClicked_correctState() {
//        // Act
//        viewModel.onRecordButtonClicked()
//
//        // Assert
//        assertEquals(RecordStateRecording::class, viewModel.recordState.value!!::class)
//    }
//
//    @Test
//    fun recordAudioVM_pauseButtonClicked_correctState() {
//        // Act
//        viewModel.onPauseButtonClicked()
//
//        // Assert
//        assertEquals(RecordStatePaused::class, viewModel.recordState.value!!::class)
//    }
//
//    @Test
//    fun recordAudioVM_stopButtonClicked_correctState() {
//        // Act
//        viewModel.onStopButtonClicked()
//
//        // Assert
//        assertEquals(RecordStateStopped::class, viewModel.recordState.value!!::class)
//    }
}