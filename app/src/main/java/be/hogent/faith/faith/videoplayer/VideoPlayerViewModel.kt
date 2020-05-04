package be.hogent.faith.faith.videoplayer

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.detail.Detail

/**
 * Knows the player and current video
 */
class VideoPlayerViewModel : ViewModel() {

    private var _player = MutableLiveData<FaithVideoPlayer>()
    val player: LiveData<FaithVideoPlayer>
        get() = _player

    private var _currentVideo = MutableLiveData<Detail>()
    val currentVideo: LiveData<Detail>
        get() = _currentVideo

    fun setPlayer(faithVideoPlayer: FaithVideoPlayer) {
        _player.postValue(faithVideoPlayer)
    }

    fun resetPlayer() {
        _player.postValue(null)
    }

    fun playNewVideo(detail: Detail, context: Context) {
        _player.value!!.playNewVideo(detail, context)
    }

    fun setCurrentVideo(detail: Detail) {
        _currentVideo.postValue(detail)
    }
}