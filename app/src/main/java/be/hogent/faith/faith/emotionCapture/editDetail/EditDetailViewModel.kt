package be.hogent.faith.faith.emotionCapture.editDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class EditDetailViewModel : ViewModel() {

    private val _emotionAvatarButtonClicked = SingleLiveEvent<Unit>()
    val emotionAvatarButtonClicked: LiveData<Unit>
        get() = _emotionAvatarButtonClicked

    private val _sendButtonClicked = SingleLiveEvent<Unit>()
    val sendButtonClicked: LiveData<Unit>
        get() = _sendButtonClicked

    fun onGoToEmotionAvatarButtonClicked() {
        _emotionAvatarButtonClicked.call()
    }

    fun onSendButtonClicked() {
        _sendButtonClicked.call()
    }
}