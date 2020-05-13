package be.hogent.faith.faith.videoplayer

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail

/**
 * Knows the player and current video
 */
class VideoPlayerViewModel : ViewModel() {

    private var _currentVideo = MutableLiveData<Detail>()
    val currentVideo: LiveData<Detail>
        get() = _currentVideo

    fun setCurrentVideo(detail: Detail) {
        _currentVideo.postValue(detail)
    }
}