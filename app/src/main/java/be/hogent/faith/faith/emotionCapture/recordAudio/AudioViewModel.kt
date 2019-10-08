package be.hogent.faith.faith.emotionCapture.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState.PlayStateInitial
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState.RecordStateInitial
import java.util.Timer
import kotlin.concurrent.timer

class AudioViewModel : ViewModel(), AudioContext {
    private val _audioState = MutableLiveData<AudioState>()
    val audioState: LiveData<AudioState>
        get() = _audioState

    private val _existingDetail = MutableLiveData<AudioDetail>()

    override var finishedRecording = false

    private var recordingTimer: Timer? = null

    /**
     *
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    val pauseSupported = MutableLiveData<Boolean>()

    val playButtonEnabled = MutableLiveData<Boolean>()
    val pauseButtonEnabled = MutableLiveData<Boolean>()
    val stopButtonEnabled = MutableLiveData<Boolean>()
    val recordButtonEnabled = MutableLiveData<Boolean>()

    private val _saveButtonVisible = MutableLiveData<Boolean>()
    val saveButtonVisible: LiveData<Boolean>
        get() = _saveButtonVisible

    val recordingTimeVisibility = MutableLiveData<Int>()

    private val _recordingTime = MutableLiveData<Int>()
    val recordingTimeString: LiveData<String> = Transformations.map(_recordingTime) { time ->
        val minutesPadded = (time / 60).toString().padStart(2, '0')
        val secondsPadded = (time % 60).toString().padStart(2, '0')
        return@map "$minutesPadded:$secondsPadded"
    }

    init {
        // Can be set to false once an existing Detail is added
        _saveButtonVisible.value = true
        _recordingTime.value = 0
    }

    /**
     * Should be called after Audio permissions are granted in the [RecordAudioFragment].
     * This is because initialising the recording without the correct permissions results
     * in a crash.
     */
    fun initialiseState() {
        if (playingExistingAudioDetail()) {
            goToNextState(PlayStateInitial(this, _existingDetail.value!!))
        } else {
            goToNextState(RecordStateInitial(this))
        }
        updateButtonVisibilityStates()
    }

    override fun goToNextState(audioState: AudioState) {
        _audioState.value = audioState
        updateButtonVisibilityStates()
    }

    private fun updateButtonVisibilityStates() {
        playButtonEnabled.value = _audioState.value?.playButtonEnabled
        pauseButtonEnabled.value = _audioState.value?.pauseButtonEnabled
        stopButtonEnabled.value = _audioState.value?.stopButtonEnabled
        recordButtonEnabled.value =
            if (playingExistingAudioDetail()) {
                false // Never allow starting a recording when listening to an existing Detail.
            } else {
                _audioState.value?.recordButtonEnabled
            }
        _saveButtonVisible.value = finishedRecording

        recordingTimeVisibility.value = _audioState.value?.recordingTimeVisibility
    }

    fun onRecordButtonClicked() {
        _audioState.value?.onRecordPressed()
        recordingTimer = timer(initialDelay = 1000, period = 1000) {
            _recordingTime.postValue(_recordingTime.value!! + 1)
        }
    }

    fun onStopButtonClicked() {
        _audioState.value?.onStopPressed()
        recordingTimer?.cancel()
        _recordingTime.postValue(0)
    }

    fun onPauseButtonClicked() {
        _audioState.value?.onPausePressed()
        recordingTimer?.cancel()
    }

    fun onPlayButtonClicked() {
        _audioState.value?.onPlayPressed()
        recordingTimer?.cancel()
    }

    override fun onCleared() {
        _audioState.value?.release()
    }

    private fun playingExistingAudioDetail(): Boolean {
        return _existingDetail.value != null
    }

    fun loadExistingAudioDetail(givenDetail: AudioDetail) {
        _existingDetail.value = givenDetail
        _saveButtonVisible.value = false
    }
}