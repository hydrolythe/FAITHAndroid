package be.hogent.faith.faith.details.audio

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.faith.details.audio.audioStates.AudioState
import be.hogent.faith.faith.details.audio.audioStates.playState.PlayStateInitial
import be.hogent.faith.faith.details.audio.audioStates.recordState.RecordStateInitial
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.service.usecases.audioDetail.CreateAudioDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber
import java.util.Timer
import kotlin.concurrent.timer

class AudioDetailViewModel(
    private val createAudioDetailUseCase: CreateAudioDetailUseCase,
    private val tempFileProvider: TempFileProvider
) : ViewModel(), AudioContext, DetailViewModel<AudioDetail> {

    private val _savedDetail = MutableLiveData<AudioDetail>()
    override val savedDetail: LiveData<AudioDetail> = _savedDetail

    private lateinit var audioState: AudioState

    private var existingDetail: AudioDetail? = null

    override var finishedRecording = false

    private var recordingTimer: Timer? = null

    /**
     *
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    var pauseSupported: Boolean = true

    private val _playButtonEnabled = MutableLiveData<Boolean>()
    val playButtonEnabled: LiveData<Boolean> = _playButtonEnabled
    private val _pauseButtonEnabled = MutableLiveData<Boolean>()
    val pauseButtonEnabled: LiveData<Boolean> = _pauseButtonEnabled
    private val _stopButtonEnabled = MutableLiveData<Boolean>()
    val stopButtonEnabled: LiveData<Boolean> = _stopButtonEnabled
    private val _recordButtonEnabled = MutableLiveData<Boolean>()
    val recordButtonEnabled: LiveData<Boolean> = _recordButtonEnabled
    private val _saveButtonVisible = MutableLiveData<Boolean>()
    val saveButtonVisible: LiveData<Boolean> = _saveButtonVisible

    private val _recordingTimeVisibility = MutableLiveData<Int>()
    val recordingTimeVisibility: LiveData<Int> = _recordingTimeVisibility

    private val _recordingTime = MutableLiveData<Int>()
    val recordingTimeString: LiveData<String> = Transformations.map(_recordingTime) { time ->
        val minutesPadded = (time / 60).toString().padStart(2, '0')
        val secondsPadded = (time % 60).toString().padStart(2, '0')
        return@map "$minutesPadded:$secondsPadded"
    }

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

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
            goToNextState(PlayStateInitial(this, existingDetail!!))
        } else {
            goToNextState(RecordStateInitial(this))
        }
        updateButtonVisibilityStates()
    }

    override fun goToNextState(audioState: AudioState) {
        this.audioState = audioState
        updateButtonVisibilityStates()
    }

    private fun updateButtonVisibilityStates() {
        _playButtonEnabled.value = audioState.playButtonEnabled
        _pauseButtonEnabled.value = pauseSupported && audioState.pauseButtonEnabled
        _stopButtonEnabled.value = audioState.stopButtonEnabled
        _recordButtonEnabled.value =
            if (playingExistingAudioDetail()) {
                false // Never allow starting a recording when listening to an existing Detail.
            } else {
                audioState.recordButtonEnabled
            }
        _saveButtonVisible.value = finishedRecording

        _recordingTimeVisibility.value = audioState.recordingTimeVisibility
    }

    fun onRecordButtonClicked() {
        audioState.onRecordPressed()
        recordingTimer = timer(initialDelay = 1000, period = 1000) {
            _recordingTime.postValue(_recordingTime.value!! + 1)
        }
    }

    fun onStopButtonClicked() {
        audioState.onStopPressed()
        recordingTimer?.cancel()
        _recordingTime.postValue(0)
    }

    fun onPauseButtonClicked() {
        audioState.onPausePressed()
        recordingTimer?.cancel()
    }

    fun onPlayButtonClicked() {
        audioState.onPlayPressed()
        recordingTimer?.cancel()
    }

    override fun onSaveClicked() {
        val params = CreateAudioDetailUseCase.Params(tempFileProvider.tempAudioRecordingFile)
        createAudioDetailUseCase.execute(params, CreateDrawingDetailUseCaseHandler())
    }

    private inner class CreateDrawingDetailUseCaseHandler :
        DisposableSingleObserver<AudioDetail>() {
        override fun onSuccess(createdDetail: AudioDetail) {
            _savedDetail.value = createdDetail
            audioState.reset()
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
            Timber.e(e)
        }
    }

    override fun onCleared() {
        audioState.release()
    }

    private fun playingExistingAudioDetail(): Boolean {
        return existingDetail != null
    }

    override fun loadExistingDetail(existingDetail: AudioDetail) {
        this.existingDetail = existingDetail
        _saveButtonVisible.value = false
        _recordButtonEnabled.value = false
    }
}