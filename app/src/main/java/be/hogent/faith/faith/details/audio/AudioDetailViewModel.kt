package be.hogent.faith.faith.details.audio

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.audio.audioPlayer.PlaybackInfoListener
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener.RecordingState.INVALID
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener.RecordingState.RECORDING
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener.RecordingState.RESET
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener.RecordingState.STOPPED
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

class AudioDetailViewModel(
    private val createAudioDetailUseCase: CreateAudioDetailUseCase,
    private val tempFileProvider: TempFileProvider
) : ViewModel(), DetailViewModel<AudioDetail> {

    private val _savedDetail = MutableLiveData<AudioDetail>()
    override val savedDetail: LiveData<AudioDetail> = _savedDetail

    private var existingDetail: AudioDetail? = null

    /**
     *
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    var pauseSupported: Boolean = false

    private val _saveButtonVisible = MutableLiveData<Boolean>()
    val saveButtonVisible: LiveData<Boolean> = _saveButtonVisible

    private val _viewState = MutableLiveData<AudioViewState>()
    val viewState: LiveData<AudioViewState> = _viewState

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    val recordingDuration = MutableLiveData<Int>()

    private val _playButtonClicked = SingleLiveEvent<Unit>()
    val playButtonClicked: LiveData<Unit> = _playButtonClicked

    private val _pauseButtonClicked = SingleLiveEvent<Unit>()
    val pauseButtonClicked: LiveData<Unit> = _pauseButtonClicked

    private val _recordStopButtonClicked = SingleLiveEvent<Unit>()
    val recordStopButtonClicked: LiveData<Unit> = _recordStopButtonClicked

    private val _playStopButtonClicked = SingleLiveEvent<Unit>()
    val playStopButtonClicked: LiveData<Unit> = _playStopButtonClicked

    private val _recordButtonClicked = SingleLiveEvent<Unit>()
    val recordButtonClicked: LiveData<Unit> = _recordButtonClicked

    init {
        // Can be set to false once an existing Detail is added
        _saveButtonVisible.value = true
    }

    /**
     * Should be called after Audio permissions are granted in the [RecordAudioFragment].
     * This is because initialising the recording without the correct permissions results
     * in a crash.
     */
    fun initialiseState() {
        if (playingExistingAudioDetail()) {
            _viewState.value = AudioViewState.FinishedRecording
        } else {
            _viewState.value = AudioViewState.Initial
        }
        updateUIVisibilityStates()
    }

    private fun updateUIVisibilityStates() {
        _saveButtonVisible.value = true
    }

    fun onRecordButtonClicked() {
        _recordButtonClicked.call()
    }

    fun onRecordStopButtonClicked() {
        _recordStopButtonClicked.call()
    }

    fun onPlayStopButtonClicked() {
        _playStopButtonClicked.call()
    }

    fun onPauseButtonClicked() {
        _pauseButtonClicked.call()
    }

    fun onPlayButtonClicked() {
        _playButtonClicked.call()
    }

    override fun onSaveClicked() {
        val params = CreateAudioDetailUseCase.Params(tempFileProvider.tempAudioRecordingFile)
        createAudioDetailUseCase.execute(params, CreateAudioDetailUseCaseHandler())
    }

    private inner class CreateAudioDetailUseCaseHandler :
        DisposableSingleObserver<AudioDetail>() {
        override fun onSuccess(createdDetail: AudioDetail) {
            _savedDetail.value = createdDetail
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
            Timber.e(e)
        }
    }

    private fun playingExistingAudioDetail(): Boolean {
        return existingDetail != null
    }

    override fun loadExistingDetail(existingDetail: AudioDetail) {
        this.existingDetail = existingDetail
        _saveButtonVisible.value = false
    }

    fun onPlayStateChanged(state: PlaybackInfoListener.PlaybackState) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    fun onRecordingStateChanged(state: RecordingInfoListener.RecordingState) {
        when (state) {
            INVALID, RESET -> _viewState.value = AudioViewState.Initial
            RECORDING -> _viewState.value = AudioViewState.Recording
            STOPPED -> _viewState.value = AudioViewState.FinishedRecording
        }
    }
}