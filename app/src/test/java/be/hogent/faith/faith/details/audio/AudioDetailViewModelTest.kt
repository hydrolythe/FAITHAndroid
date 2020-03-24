package be.hogent.faith.faith.details.audio

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.faith.TestUtils.getValue
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener
import be.hogent.faith.faith.di.appModule
import be.hogent.faith.faith.testModule
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
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

    @Test
    fun `on startUp the UI is in the Initial state`() {
        // Arrange
        viewModel.initialiseState()

        assertEquals(AudioViewState.Initial, getValue(viewModel.viewState))
    }

    @Test
    fun `when given an existing detail the UI starts in the finishedRecording state`() {
        // Arrange
        viewModel.loadExistingDetail(mockk())
        viewModel.initialiseState()

        assertEquals(AudioViewState.FinishedRecording, getValue(viewModel.viewState))
    }

    @Test
    fun `the pause button is not visible when recording when pausing is not supported`() {
        // Arrange
        viewModel.initialiseState()
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.RECORDING)
        viewModel.pauseSupported = false

        // Act
        val pauseVisible = getValue(viewModel.recordPauseButtonVisible)

        // Assert
        assertFalse(pauseVisible)
    }

    @Test
    fun `the pause button is visible when recording when pausing is supported`() {
        // Arrange
        viewModel.initialiseState()
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.RECORDING)
        viewModel.pauseSupported = true

        // Act
        val pauseVisible = getValue(viewModel.recordPauseButtonVisible)

        // Assert
        assertTrue(pauseVisible)
    }

    @Test
    fun `when pausing the recording the pause button is invisible`() {
        // Arrange
        viewModel.initialiseState()
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.RECORDING)
        viewModel.pauseSupported = true

        // Act
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.PAUSED)
        val pauseVisible = getValue(viewModel.recordPauseButtonVisible)

        // Assert
        assertFalse(pauseVisible)
    }

    @Test
    fun `when pausing the recording the record button is visible`() {
        // Arrange
        viewModel.initialiseState()
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.RECORDING)
        viewModel.pauseSupported = true

        // Act
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.PAUSED)
        val restartRecordingButtonVisible = getValue(viewModel.recordRestartButtonVisible)

        // Assert
        assertTrue(restartRecordingButtonVisible)
    }

    @Test
    fun `resetting the recording is not available when playing an existing detail`() {
        // Arrange
        viewModel.loadExistingDetail(mockk())
        viewModel.initialiseState()
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.STOPPED)

        // Act
        val resetVisible = getValue(viewModel.deleteButtonVisible)

        // Assert
        assertFalse(resetVisible)
    }

    @Test
    fun `resetting the recording is available when playing a recording you just recorded`() {
        // Arrange
        viewModel.initialiseState()
        viewModel.onRecordingStateChanged(RecordingInfoListener.RecordingState.STOPPED)

        // Act
        val resetVisible = getValue(viewModel.deleteButtonVisible)

        // Assert
        assertTrue(resetVisible)
    }

    @Test
    fun `when setting the final recording duration it is shown in a mm_colon_ss format`() {
        // Act
        viewModel.setRecordingFinalDuration(125_000) // 125 seconds

        // Assert
        assertEquals("02:05", getValue(viewModel.recordingDuration))
    }

    @Test
    fun `when setting the final recording duration the time is rounded up to the nearest second`() {
        // Act
        viewModel.setRecordingFinalDuration(600)

        // Assert
        assertEquals("00:01", getValue(viewModel.recordingDuration))
    }

    @Test
    fun `when setting the final recording duration the time is rounded down to the nearest second`() {
        // Act
        viewModel.setRecordingFinalDuration(400)

        // Assert
        assertEquals("00:00", getValue(viewModel.recordingDuration))
    }
}
