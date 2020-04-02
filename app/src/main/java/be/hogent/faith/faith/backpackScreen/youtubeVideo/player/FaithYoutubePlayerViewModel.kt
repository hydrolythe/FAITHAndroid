package be.hogent.faith.faith.backpackScreen.youtubeVideo.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Player and its current state
 */
class FaithYoutubePlayerViewModel : ViewModel() {

    private var _currentState = MutableLiveData<VideoPlayerState>()
    val currentState: LiveData<VideoPlayerState>
        get() = _currentState

    private var _player = MutableLiveData<FaithYoutubePlayer>()
    val player: LiveData<FaithYoutubePlayer>
        get() = _player

    init {
        _currentState.postValue(VideoPlayerState.UNSTARTED)
    }

    fun onStopClicked() {
        _currentState.postValue(VideoPlayerState.STOPPED)
    }

    fun onPlayClicked() {
        _currentState.postValue(VideoPlayerState.PLAYING)
    }

    fun onPauseClicked() {
        _currentState.postValue(VideoPlayerState.PAUSED)
    }

    fun setPlayer(faithYoutubePlayer: FaithYoutubePlayer) {
        _player.postValue(faithYoutubePlayer)
    }

    fun resetPlayer() {
        _player.postValue(null)
    }
}