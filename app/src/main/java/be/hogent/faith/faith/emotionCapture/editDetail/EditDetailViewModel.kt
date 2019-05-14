package be.hogent.faith.faith.emotionCapture.editDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class EditDetailViewModel : ViewModel() {

    private val _emotionAvatarButtonClicked = SingleLiveEvent<Unit>()
    val emotionAvatarButtonClicked: LiveData<Unit>
        get() = _emotionAvatarButtonClicked

    fun onGoToEmotionAvatarButtonClicked() {
        _emotionAvatarButtonClicked.call()
    }
}