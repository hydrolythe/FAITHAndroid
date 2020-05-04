package be.hogent.faith.faith.videoplayer

/**
 * All the methods needed to play a new video
 */
interface IVideoPlayer {
    fun playVideo()
    fun resumeVideo()
    fun pauseVideo()
    fun stopVideo()
    fun seekTo(time: Float)
    fun stopPlayer()
}