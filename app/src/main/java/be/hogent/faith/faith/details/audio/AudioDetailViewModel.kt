package be.hogent.faith.faith.details.audio

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.audio.audioStates.AudioContext
import be.hogent.faith.faith.details.audio.audioStates.AudioState
import be.hogent.faith.faith.details.audio.audioStates.playState.PlayStateInitial
import be.hogent.faith.faith.details.audio.audioStates.recordState.RecordStateInitial
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.service.usecases.detail.audioDetail.CreateAudioDetailUseCase
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

class AudioDetailViewModel(
    private val createAudioDetailUseCase: CreateAudioDetailUseCase,
    private val tempFileProvider: TempFileProvider
) : ViewModel(), AudioContext, DetailViewModel<AudioDetail> {

    private val _savedDetail = MutableLiveData<AudioDetail>()
    override val savedDetail: LiveData<AudioDetail> = _savedDetail

    private lateinit var audioState: AudioState

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
            goToNextState(PlayStateInitial(this, existingDetail!!))
        } else {
            goToNextState(RecordStateInitial(this))
        }
        updateUIVisibilityStates()
    }

    override fun goToNextState(audioState: AudioState) {
        this.audioState = audioState
        updateUIVisibilityStates()
    }

    private fun updateUIVisibilityStates() {
        _saveButtonVisible.value = true
        _viewState.value = audioState.audioViewState
    }

    fun onRecordButtonClicked() {
        audioState.onRecordPressed()
    }

    fun onStopButtonClicked() {
        audioState.onStopPressed()
    }

    fun onPauseButtonClicked() {
        audioState.onPausePressed()
    }

    fun onPlayButtonClicked() {
        audioState.onPlayPressed()
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
    }
}