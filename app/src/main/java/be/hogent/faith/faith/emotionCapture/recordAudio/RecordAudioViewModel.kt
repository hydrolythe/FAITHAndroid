package be.hogent.faith.faith.emotionCapture.recordAudio

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioContext
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.AudioState
import be.hogent.faith.faith.emotionCapture.recordAudio.audioStates.recordState.RecordStateInitial

class RecordAudioViewModel : ViewModel(), AudioContext {

    private val _audioState = MutableLiveData<AudioState>()
    val audioState: LiveData<AudioState>
        get() = _audioState

    /**
     *
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    val pauseSupported = MutableLiveData<Boolean>()

    val playButtonVisible = MutableLiveData<Int>()
    val pauseButtonVisible = MutableLiveData<Int>()
    val stopButtonVisible = MutableLiveData<Int>()
    val recordButtonVisible = MutableLiveData<Int>()

    init {
        playButtonVisible.value = View.INVISIBLE
        pauseButtonVisible.value = View.INVISIBLE
        stopButtonVisible.value = View.INVISIBLE
        recordButtonVisible.value = View.VISIBLE
    }

    /**
     * Should be called after Audio permissions are granted in the [RecordAudioFragment].
     * This is because initialising the recording without the correct permissions results
     * in a crash.
     */
    fun initialiseState() {
        goToNextState(RecordStateInitial(this))
    }

    override fun goToNextState(audioState: AudioState) {
        _audioState.value = audioState
        updateButtonVisibilityStates()
    }

    private fun updateButtonVisibilityStates() {
        playButtonVisible.value = _audioState.value?.playButtonVisible
        pauseButtonVisible.value = _audioState.value?.pauseButtonVisible
        stopButtonVisible.value = _audioState.value?.stopButtonVisible
        recordButtonVisible.value = _audioState.value?.recordButtonVisible
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
}