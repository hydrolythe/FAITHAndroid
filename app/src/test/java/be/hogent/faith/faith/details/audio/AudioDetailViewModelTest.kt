package be.hogent.faith.faith.details.audio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class AudioDetailViewModelTest : KoinTest {

    private val createAudioDetailUseCase = mockk<CreateAudioDetailUseCase>()

    private lateinit var viewModel: AudioDetailViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin { modules(listOf(appModule, testModule)) }
        viewModel = AudioDetailViewModel(createAudioDetailUseCase, get())
        viewModel.initialiseState()
    }

    @After
    fun takeDown() {
        stopKoin()
    }

//    @Test
//    fun audioDetailVM_existingDetail_recordButtonNotAvailable() {
//        // Act
//        viewModel.loadExistingDetail(mockk())
//
//        // Assert
//        assertFalse(getValue(viewModel.recordButtonEnabled))
//    }
//
//    @Test
//    fun audioDetailVM_existingDetail_saveButtonNotVisible() {
//        // Act
//        viewModel.loadExistingDetail(mockk())
//
//        // Assert
//        assertFalse(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_initial_correctButtonsEnabled() {
//        // Assert
//        assertFalse(getValue(viewModel.playButtonEnabled))
//        assertFalse(getValue(viewModel.pauseButtonEnabled))
//        assertFalse(getValue(viewModel.stopButtonEnabled))
//        assertTrue(getValue(viewModel.recordButtonEnabled))
//
//        assertFalse(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_onRecording_pauseSupported_correctButtonsEnabled() {
//        // Arrange
//        viewModel.pauseSupported = true
//
//        // Act
//        viewModel.onRecordButtonClicked()
//
//        // Assert
//        assertFalse(getValue(viewModel.playButtonEnabled))
//        assertTrue(getValue(viewModel.pauseButtonEnabled))
//        assertTrue(getValue(viewModel.stopButtonEnabled))
//        assertFalse(getValue(viewModel.recordButtonEnabled))
//
//        assertFalse(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_onRecording_pauseNotSupported_correctButtonsEnabled() {
//        // Act
//        viewModel.pauseSupported = false
//        viewModel.onRecordButtonClicked()
//
//        // Assert
//        assertFalse(getValue(viewModel.playButtonEnabled))
//        assertFalse(getValue(viewModel.pauseButtonEnabled))
//        assertTrue(getValue(viewModel.stopButtonEnabled))
//        assertFalse(getValue(viewModel.recordButtonEnabled))
//
//        assertFalse(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_onRecordingPaused_correctButtonsEnabled() {
//        // Act
//        viewModel.onRecordButtonClicked()
//        viewModel.onPauseButtonClicked()
//
//        // Assert
//        assertFalse(getValue(viewModel.playButtonEnabled))
//        assertFalse(getValue(viewModel.pauseButtonEnabled))
//        assertTrue(getValue(viewModel.stopButtonEnabled))
//        assertTrue(getValue(viewModel.recordButtonEnabled))
//
//        assertFalse(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_onRecordingStopped_correctButtonsEnabled() {
//        // Act
//        viewModel.onRecordButtonClicked()
//        viewModel.onStopButtonClicked()
//
//        // Assert
//        assertTrue(getValue(viewModel.playButtonEnabled))
//        assertFalse(getValue(viewModel.pauseButtonEnabled))
//        assertFalse(getValue(viewModel.stopButtonEnabled))
//        assertTrue(getValue(viewModel.recordButtonEnabled))
//
//        assertTrue(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_onRecordingPlaying_correctButtonsEnabled() {
//        viewModel.pauseSupported = true
//        // Act
//        viewModel.onRecordButtonClicked()
//        viewModel.onStopButtonClicked()
//        viewModel.onPlayButtonClicked()
//
//        // Assert
//        assertFalse(getValue(viewModel.playButtonEnabled))
//        assertTrue(getValue(viewModel.pauseButtonEnabled))
//        assertTrue(getValue(viewModel.stopButtonEnabled))
//        assertFalse(getValue(viewModel.recordButtonEnabled))
//
//        assertTrue(getValue(viewModel.saveButtonVisible))
//    }
//
//    @Test
//    fun audioDetailVM_onRecordingPlayingPaused_correctButtonsEnabled() {
//        viewModel.pauseSupported = true
//        // Act
//        viewModel.onRecordButtonClicked()
//        viewModel.onStopButtonClicked()
//        viewModel.onPlayButtonClicked()
//        viewModel.onPauseButtonClicked()
//
//        // Assert
//        assertTrue(getValue(viewModel.playButtonEnabled))
//        assertFalse(getValue(viewModel.pauseButtonEnabled))
//        assertTrue(getValue(viewModel.stopButtonEnabled))
//        assertFalse(getValue(viewModel.recordButtonEnabled))
//
//        assertTrue(getValue(viewModel.saveButtonVisible))
//    }
}
