package be.hogent.faith.faith.emotionCapture.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.playState.PlayStateInitial
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState.RecordStateInitial

class RecordAudioViewModel : ViewModel(), AudioContext {

    private val _audioState = MutableLiveData<AudioState>()
    val audioState: LiveData<AudioState>
        get() = _audioState

    private val _existingDetail = MutableLiveData<AudioDetail>()
    val existingDetail: LiveData<AudioDetail>
        get() = _existingDetail

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

    /**
     * Should be called after Audio permissions are granted in the [RecordAudioFragment].
     * This is because initialising the recording without the correct permissions results
     * in a crash.
     */
    fun initialiseState() {
        if (playingExistingAudioDetail()) {
            goToNextState(PlayStateInitial(this, existingDetail.value!!))
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
    }

    fun onRecordButtonClicked() {
        _audioState.value?.onRecordPressed()
    }

    fun onStopButtonClicked() {
        _audioState.value?.onStopPressed()
    }

    fun onPauseButtonClicked() {
        _audioState.value?.onPausePressed()
    }

    fun onPlayButtonClicked() {
        _audioState.value?.onPlayPressed()
    }

    override fun onCleared() {
        _audioState.value?.release()
    }

    private fun playingExistingAudioDetail(): Boolean {
        return _existingDetail.value != null
    }
}