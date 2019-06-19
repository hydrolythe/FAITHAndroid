package be.hogent.faith.faith.emotionCapture.recordAudio

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.playState.PlayContext
import be.hogent.faith.faith.emotionCapture.recordAudio.playState.PlayState
import be.hogent.faith.faith.emotionCapture.recordAudio.playState.PlayStateInitial
import be.hogent.faith.faith.emotionCapture.recordAudio.playState.PlayStatePlaying
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordState
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordStateInitial
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordStatePaused
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordStateRecording
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordStateStopped
import be.hogent.faith.faith.emotionCapture.recordAudio.recordState.RecordingContext
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.util.TAG

class RecordAudioViewModel(
    val tempFileProvider: TempFileProvider
) : RecordingContext, PlayContext, ViewModel() {

    private val _recordState = MutableLiveData<RecordState>()
    val recordState: LiveData<RecordState>
        get() = _recordState

    private val _playState = MutableLiveData<PlayState>()
    val playState: LiveData<PlayState>
        get() = _playState

    /**
     *
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    val pauseSupported = MutableLiveData<Boolean>()

    val pauseButtonVisible = MediatorLiveData<Int>()

    val playButtonVisible = MediatorLiveData<Int>()

    init {
        pauseSupported.value = false
        _recordState.value = RecordStateInitial(this, tempFileProvider)
        _playState.value = PlayStateInitial(this, tempFileProvider)

        configurePauseButtonVisibility()
        configurePlayButtonVisibility()
    }

    private fun configurePlayButtonVisibility() {
        playButtonVisible.apply {
            addSource(_playState) { playState ->
                when (playState) {
                    is PlayStatePlaying -> value = View.GONE
                    else -> value = View.VISIBLE
                }
            }
            addSource(_recordState) { recordState ->
                when (recordState) {
                    is RecordStateStopped -> value = View.VISIBLE
                    else -> value = View.GONE
                }
            }
        }
    }

    private fun configurePauseButtonVisibility() {
        pauseButtonVisible.apply {
            if (!pauseSupported.value!!) {
                value = View.GONE
            }

            addSource(_recordState) { recordState ->
                when (recordState) {
                    is RecordStateRecording -> value = View.VISIBLE
                    else -> value = View.GONE
                }
            }
            addSource(_playState) { playState ->
                when (playState) {
                    is PlayStatePlaying -> value = View.VISIBLE
                    else -> value = View.GONE
                }
            }
        }
    }

    override fun goToRecordState(newState: RecordState) {
        _recordState.postValue(newState)
    }

    override fun goToPlayState(newState: PlayState) {
        _playState.postValue(newState)
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

    private val _playButtonClicked = SingleLiveEvent<Unit>()
    val playButtonClicked: LiveData<Unit>
        get() = _playButtonClicked

    private val _saveButtonClicked = SingleLiveEvent<Unit>()
    val saveButtonClicked: LiveData<Unit>
        get() = _saveButtonClicked

    fun onRecordButtonClicked() {
        Log.d(TAG, "Record pressed")
        recordState.value!!.onRecordPressed()
        _recordButtonClicked.call()
    }

    fun onRestartButtonClicked() {
        Log.d(TAG, "Restart pressed")
        recordState.value!!.onRestartPressed()
        _restartButtonClicked.call()
    }

    fun onStopButtonClicked() {
        Log.d(TAG, "Stop pressed")
        recordState.value!!.onStopPressed()
        playState.value!!.onStopPressed()
        _stopButtonClicked.call()
    }

    fun onPauseButtonClicked() {
        Log.d(TAG, "Pause pressed")
        if (pauseSupported.value!!) {
            recordState.value!!.onPausePressed()
            _pauseButtonClicked.call()
        }
        playState.value!!.onPausePressed()
    }

    fun onPlayButtonClicked() {
        Log.d(TAG, "Play pressed")
        _playState.value!!.onPlayPressed()
        _playButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        _saveButtonClicked.call()
    }

    val stopButtonVisible: LiveData<Int> = Transformations.map<RecordState, Int>(_recordState) { recordState ->
        when (recordState) {
            is RecordStateStopped -> View.GONE
            else -> View.VISIBLE
        }
    }
    val recordButtonVisible: LiveData<Int> = Transformations.map<RecordState, Int>(_recordState) { recordState ->
        when (recordState) {
            is RecordStateInitial -> View.VISIBLE
            is RecordStatePaused -> View.VISIBLE
            is RecordStateStopped -> View.VISIBLE
            else -> View.GONE
        }
    }

    val restartButtonVisible: LiveData<Int> = Transformations.map<RecordState, Int>(_recordState) { recordState ->
        when (recordState) {
            is RecordStateInitial -> View.GONE
            else -> View.VISIBLE
        }
    }
}