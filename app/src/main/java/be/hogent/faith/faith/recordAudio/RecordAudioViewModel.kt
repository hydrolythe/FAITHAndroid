package be.hogent.faith.faith.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class RecordAudioViewModel : ViewModel() {

    private val _recordingStatus = MutableLiveData<RecordingStatus>()
    val recordingStatus: LiveData<RecordingStatus>
        get() = _recordingStatus

    init {
        _recordingStatus.value = RecordingStatus.INITIAL
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

    enum class RecordingStatus {
        RECORDING,
        PAUSED,
        STOPPED,
        INITIAL
    }
}