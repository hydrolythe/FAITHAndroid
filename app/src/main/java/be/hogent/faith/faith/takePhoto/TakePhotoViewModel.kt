package be.hogent.faith.faith.takePhoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class TakePhotoViewModel : ViewModel() {
    private val _takePhotoButtonClicked = SingleLiveEvent<Unit>()
    val takePhotoButtonClicked: LiveData<Unit>
        get() = _takePhotoButtonClicked

    private val _emotionAvatarButtonClicked = SingleLiveEvent<Unit>()
    val emotionAvatarButtonClicked: LiveData<Unit>
        get() = _emotionAvatarButtonClicked

    fun onTakePhotoButtonClicked() {
        _takePhotoButtonClicked.call()
    }

    fun onGoToEmotionAvatarButtonClicked() {
        _emotionAvatarButtonClicked.call()
    }
}