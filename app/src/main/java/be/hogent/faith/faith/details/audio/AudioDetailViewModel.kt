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

    private val audioState = MutableLiveData<AudioState>()

    private val existingDetail = MutableLiveData<AudioDetail>()

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

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

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
            goToNextState(PlayStateInitial(this, existingDetail.value!!))
        } else {
            goToNextState(RecordStateInitial(this))
        }
        updateButtonVisibilityStates()
    }

    override fun goToNextState(audioState: AudioState) {
        this.audioState.value = audioState
        updateButtonVisibilityStates()
    }

    private fun updateButtonVisibilityStates() {
        playButtonEnabled.value = audioState.value?.playButtonEnabled
        pauseButtonEnabled.value = audioState.value?.pauseButtonEnabled
        stopButtonEnabled.value = audioState.value?.stopButtonEnabled
        recordButtonEnabled.value =
            if (playingExistingAudioDetail()) {
                false // Never allow starting a recording when listening to an existing Detail.
            } else {
                audioState.value?.recordButtonEnabled
            }
        _saveButtonVisible.value = finishedRecording

        recordingTimeVisibility.value = audioState.value?.recordingTimeVisibility
    }

    fun onRecordButtonClicked() {
        audioState.value?.onRecordPressed()
        recordingTimer = timer(initialDelay = 1000, period = 1000) {
            _recordingTime.postValue(_recordingTime.value!! + 1)
        }
    }

    fun onStopButtonClicked() {
        audioState.value?.onStopPressed()
        recordingTimer?.cancel()
        _recordingTime.postValue(0)
    }

    fun onPauseButtonClicked() {
        audioState.value?.onPausePressed()
        recordingTimer?.cancel()
    }

    fun onPlayButtonClicked() {
        audioState.value?.onPlayPressed()
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
            audioState.value?.reset()
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
            Timber.e(e)
        }
    }

    override fun onCleared() {
        audioState.value?.release()
    }

    private fun playingExistingAudioDetail(): Boolean {
        return existingDetail.value != null
    }

    override fun loadExistingDetail(existingDetail: AudioDetail) {
        this.existingDetail.value = existingDetail
        _saveButtonVisible.value = false
    }
}