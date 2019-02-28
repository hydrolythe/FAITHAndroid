package be.hogent.faith.faith.recordAudio

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class RecordAudioViewModel : ViewModel() {

    private val _recordButtonClicked = SingleLiveEvent<Unit>()
    val recordButtonClicked: LiveData<Unit>
        get() = _recordButtonClicked

    fun onRecordButtonClicked() {
        _recordButtonClicked.call()
    }

}