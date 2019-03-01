package be.hogent.faith.faith.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordAudioViewModel : ViewModel() {

    private val _recordingStatus = MutableLiveData<RecordingStatus>()
    val recordingStatus: LiveData<RecordingStatus>
        get() = _recordingStatus

    val recordButtonVisible = MutableLiveData<Boolean>()
    val stopButtonVisible = MutableLiveData<Boolean>()
    val pauseButtonVisible = MutableLiveData<Boolean>()

    init {
        _recordingStatus.value = RecordingStatus.INITIAL
        recordButtonVisible.value = true
        stopButtonVisible.value = false
        pauseButtonVisible.value = false
    }


    fun onRecordButtonClicked() {
        _recordingStatus.value = RecordingStatus.RECORDING
        stopButtonVisible.value = true
        pauseButtonVisible.value = true
    }

    fun onStopButtonClicked() {
        _recordingStatus.value = RecordingStatus.STOPPED
    }

    fun onPauseButtonClicked() {
        _recordingStatus.value = RecordingStatus.PAUSED
        //TODO: complete other status changes
    }

    enum class RecordingStatus {
        RECORDING,
        PAUSED,
        STOPPED,
        INITIAL
    }
}