package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

interface IVideoPlayer {

    fun playVideo(currentState: VideoPlayerState)
    fun pauseVideo(currentState: VideoPlayerState)
    fun stopVideo(currentState: VideoPlayerState)
    fun seekTo(currentState: VideoPlayerState, time : Float)
    fun setFullScreen()
}