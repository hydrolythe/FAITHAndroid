package be.hogent.faith.faith.details.audio

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.details.audio.audioPlayer.PlaybackInfoListener
import be.hogent.faith.faith.details.audio.audioRecorder.RecordingInfoListener.RecordingState
import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.faith.util.combineWith
import be.hogent.faith.util.toMinutesSecondString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File
import kotlin.math.roundToLong

class AudioDetailViewModel(
    private val tempFileProvider: TempFileProvider,
    val audioDetailRepository: IAudioDetailRepository
) : ViewModel(), DetailViewModel<AudioDetail> {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _savedDetail = MutableLiveData<AudioDetail>()
    override val savedDetail: LiveData<AudioDetail> = _savedDetail

    private var existingDetail: AudioDetail? = null
    var tempFile: File

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

    /**
     *
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    var pauseSupported: Boolean = false

    private val _viewState = MutableLiveData<AudioViewState>()
    val viewState: LiveData<AudioViewState> = _viewState

    private val _saveButtonEnabled = MutableLiveData<Boolean>()
    val saveButtonVisible: LiveData<Boolean> =
        _saveButtonEnabled.combineWith(viewState) { saveEnabled, viewState ->
            // saveEnabled is set in init so can be !!
            viewState == AudioViewState.FinishedRecording && saveEnabled!!
        }

    private val _deleteButtonEnabled = MutableLiveData<Boolean>()
    val deleteButtonVisible: LiveData<Boolean> =
        _deleteButtonEnabled.combineWith(viewState) { deleteEnabled, viewState ->
            // Delete is set in init so can be !!
            deleteEnabled!! && viewState == AudioViewState.FinishedRecording
        }

    private val _recordingPaused = MutableLiveData<Boolean>()

    val recordPauseButtonVisible: LiveData<Boolean> =
        Transformations.map(_recordingPaused) { paused ->
            !paused && pauseSupported
        }
    val recordRestartButtonVisible: LiveData<Boolean> =
        Transformations.map(_recordingPaused) { paused ->
            paused && pauseSupported
        }

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    private val _playButtonClicked = SingleLiveEvent<Unit>()
    val playButtonClicked: LiveData<Unit> = _playButtonClicked

    private val _pauseButtonClicked = SingleLiveEvent<Unit>()
    val pauseButtonClicked: LiveData<Unit> = _pauseButtonClicked

    private val _recordStopButtonClicked = SingleLiveEvent<Unit>()
    val recordStopButtonClicked: LiveData<Unit> = _recordStopButtonClicked

    private val _recordPauseButtonClicked = SingleLiveEvent<Unit>()
    val recordPauseButtonClicked: LiveData<Unit> = _recordPauseButtonClicked

    private val _playStopButtonClicked = SingleLiveEvent<Unit>()
    val playStopButtonClicked: LiveData<Unit> = _playStopButtonClicked

    private val _recordButtonClicked = SingleLiveEvent<Unit>()
    val recordButtonClicked: LiveData<Unit> = _recordButtonClicked

    private val _resetButtonClicked = SingleLiveEvent<Unit>()
    val resetButtonClicked: LiveData<Unit> = _resetButtonClicked

    private val _cancelClicked = SingleLiveEvent<Unit>()
    val cancelClicked: LiveData<Unit>
        get() = _cancelClicked

    // TODO : als encryptie geimplementeerd
    private val _file = MutableLiveData<File>()
    val file: LiveData<File>
        get() = _file

    /**
     * Indicates the recording time in seconds
     */
    private val _recordingTime = MutableLiveData<Long>()
    val recordingTime: LiveData<String> = Transformations.map(_recordingTime) {
        toMinutesSecondString(it)
    }

    /**
     * Indicates the final recording duration in ms
     */
    private val _recordingFinalDuration = MutableLiveData<Int>()
    val recordingDuration: LiveData<String> = Transformations.map(_recordingFinalDuration) {
        val roundedToNearestSeconds = ((it.toDouble()) / 1000).roundToLong()
        toMinutesSecondString(roundedToNearestSeconds)
    }

    init {
        // Can be set to false once an existing Detail is added
        _saveButtonEnabled.value = true
        _deleteButtonEnabled.value = true
        _recordingPaused.value = false

        _recordingTime.value = 0
        tempFile = tempFileProvider.tempAudioRecordingFile
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
    }

    fun onRecorderStartButtonClicked() {
        _recordButtonClicked.call()
    }

    fun onRecorderStopButtonClicked() {
        _recordStopButtonClicked.call()
    }

    fun onRecorderPauseButtonClicked() {
        _recordPauseButtonClicked.call()
    }

    fun onPlayerPauseButtonClicked() {
        _pauseButtonClicked.call()
    }

    fun onPlayerPlayButtonClicked() {
        _playButtonClicked.call()
    }

    fun onResetButtonClicked() {
        _resetButtonClicked.call()
    }

    fun onCancelClicked() {
        _cancelClicked.call()
    }

    override fun onSaveClicked() {
        uiScope.launch {
            val execution = audioDetailRepository.getAudioDetail(tempFile)
            if(execution.success!=null){
                existingDetail = execution.success
                _getDetailMetaData.call()
            }
            if(execution.exception!=null){
                _errorMessage.postValue(R.string.error_save_audio_failed)
                Timber.e(execution.exception)
            }
        }
    }

    private fun playingExistingAudioDetail(): Boolean {
        return existingDetail != null
    }

    override fun loadExistingDetail(existingDetail: AudioDetail) {
        this.existingDetail = existingDetail
        _saveButtonEnabled.value = false
        _deleteButtonEnabled.value = false
        /*
        // TODO tijdelijk tot encryptie
        if (existingDetail.file.path.startsWith("users")) {
            val params = LoadDetailFileUseCase.Params(existingDetail)
            loadDetailFile.execute(params, LoadFileUseCaseHandler())
        } else
        */
        _file.value = existingDetail.file
    }

    fun onRecordingStateChanged(state: RecordingState) {
        when (state) {
            RecordingState.INVALID, RecordingState.RESET -> _viewState.value =
                AudioViewState.Initial

            RecordingState.RECORDING -> {
                _viewState.value = AudioViewState.Recording
                _recordingPaused.value = false
            }

            RecordingState.STOPPED -> _viewState.value = AudioViewState.FinishedRecording
            RecordingState.PAUSED -> _recordingPaused.value = true
        }
    }

    /**
     * @param duration the duration in milliseconds
     */
    fun setRecordingFinalDuration(duration: Int) {
        _recordingFinalDuration.value = duration
    }

    fun updateRecordingTimer(duration: Long) {
        // Postvalue because this originates from the scheduled task in the AudioRecorderHolder
        _recordingTime.postValue(duration)
    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        existingDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = existingDetail
    }

    fun onPlayStateChanged(state: PlaybackInfoListener.PlaybackState) {
        Timber.i("Now in state $state")
    }
}