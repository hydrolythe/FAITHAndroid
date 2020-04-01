package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoPlayerStateViewModel : ViewModel(){

    private var _currentState = MutableLiveData<VideoPlayerState>()
    val currentState: LiveData<VideoPlayerState>
        get() = _currentState

    private var _isFullScreen = MutableLiveData<Boolean>()
    val isFullScreen : LiveData<Boolean>
        get() = _isFullScreen

    init {
        _currentState.postValue(VideoPlayerState.UNSTARTED)
        _isFullScreen.postValue(false)
    }

    fun setVideoPlayerState(newState: VideoPlayerState){
        _currentState.postValue(newState)
    }

    fun setFullScreen(){
        _isFullScreen.postValue(true)
    }

    fun setSmallScreen(){
        _isFullScreen.postValue(false)
    }

}