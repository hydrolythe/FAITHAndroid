package be.hogent.faith.faith.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.SaveAudioRecordingUseCase
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class RecordAudioViewModel(
    private val saveAudioUseCase: SaveAudioRecordingUseCase,
    private val event: Event
) : ViewModel() {

    /**
     * The file where the recording is temporarily saved.
     * Will be filled in by the [RecordAudioFragment] once the picture was taken.
     */
    lateinit var tempRecordingFile: File

    private val _recordingStatus = MutableLiveData<RecordingStatus>()
    val recordingStatus: LiveData<RecordingStatus>
        get() = _recordingStatus

    val recordingName = MutableLiveData<String>()

    private val disposables = CompositeDisposable()

    /**
     * True when pausing an audio recording is supported.
     * Support starts at SDK 24.
     * Default false, users of this ViewModel should change this to be true when supported.
     */
    val pauseSupported = MutableLiveData<Boolean>()

    init {
        _recordingStatus.value = RecordingStatus.INITIAL
        pauseSupported.value = false
        recordingName.value = ""
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

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit>
        get() = _cancelButtonClicked

    private val _recordingSavedSuccessFully = SingleLiveEvent<Unit>()
    val recordingSavedSuccessFully: LiveData<Unit>
        get() = _recordingSavedSuccessFully

    /**
     * Will be updated with the latest error message when an error occurs when saving the recording.
     */
    private val _recordingSaveFailed = MutableLiveData<String>()
    val recordingSaveFailed: LiveData<String>
        get() = _recordingSaveFailed

    fun onRecordButtonClicked() {
        _recordingStatus.value = RecordingStatus.RECORDING
        _recordButtonClicked.call()
    }

    fun onRestartButtonClicked() {
        _recordingStatus.value = RecordingStatus.INITIAL
        _restartButtonClicked.call()
    }

    fun onStopButtonClicked() {
        _recordingStatus.value = RecordingStatus.STOPPED
        _stopButtonClicked.call()
    }

    fun onPauseButtonClicked() {
        _recordingStatus.value = RecordingStatus.PAUSED
        _pauseButtonClicked.call()
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        val disposable = saveAudioUseCase.execute(
            SaveAudioRecordingUseCase.SaveAudioRecordingParams(
                tempRecordingFile,
                event,
                recordingName.value!!
            )
        ).subscribe({
            _recordingSavedSuccessFully.call()
            // Name has to be cleared so future pictures start with an empty recordingName
            recordingName.value = ""
        }, {
            _recordingSaveFailed.postValue(it.message)
        })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    enum class RecordingStatus {
        RECORDING,
        PAUSED,
        STOPPED,
        INITIAL
    }
}