package be.hogent.faith.faith.emotionCapture.takePhoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class TakePhotoViewModel : ViewModel() {

    private val _takePhotoButtonClicked = SingleLiveEvent<Unit>()
    val takePhotoButtonClicked: LiveData<Unit>
        get() = _takePhotoButtonClicked

    fun onTakePhotoButtonClicked() {
        _takePhotoButtonClicked.call()
    }

}