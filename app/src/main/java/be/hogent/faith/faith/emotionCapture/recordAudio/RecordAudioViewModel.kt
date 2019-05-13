package be.hogent.faith.faith.emotionCapture.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class RecordAudioViewModel : ViewModel() {

    private val _recordingStatus = MutableLiveData<RecordingStatus>()
    val recordingStatus: LiveData<RecordingStatus>
        get() = _recordingStatus

    /**
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    val pauseSupported = MutableLiveData<Boolean>()

    init {
        _recordingStatus.value = RecordingStatus.INITIAL
        pauseSupported.value = false
    }

    private val _recordButtonClicked = SingleLiveEvent<Unit>()
    val recordButtonClicked: LiveData<Unit>
        get() = _recordButtonClicked

    private val _restartButtonClicked = SingleLiveEvent<Unit>()
    val restartButtonClicked: LiveData<Unit>
        get() = _restartButtonClicked

    private val _pauseButtonClicked = SingleLiveEvent<Unit>()
    val pauseButtonClicked: LiveData<Unit>
        get() = _pauseButtonClicked

    private val _stopButtonClicked = SingleLiveEvent<Unit>()
    val stopButtonClicked: LiveData<Unit>
        get() = _stopButtonClicked

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit>
        get() = _cancelButtonClicked

    fun onRecordButtonClicked() {
        _recordingStatus.value = RecordingStatus.RECORDING
        _recordButtonClicked.call()
    }

    fun onRestartButtonClicked() {
        _recordingStatus.value = RecordingStatus.INITIAL
        _restartButtonClicked.call()
    }

    fun onStopButtonClicked() {
        _recordingStatus.value = RecordingStatus.STOPPED
        _stopButtonClicked.call()
    }

    fun onPauseButtonClicked() {
        _recordingStatus.value = RecordingStatus.PAUSED
        _pauseButtonClicked.call()
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    enum class RecordingStatus {
        RECORDING,
        PAUSED,
        STOPPED,
        INITIAL
    }
}