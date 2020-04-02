package be.hogent.faith.faith.backpackScreen.youtubeVideo.player

import android.view.View
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

/**
 * Knows the youtube controller and adds it to the player
 * Loads a new video
 * Updates the duration and current time of the video
 */
class FaithYoutubePlayerListener(
    private val faithYoutubePlayer: FaithYoutubePlayer,
    private val customPlayerUi: View
) : AbstractYouTubePlayerListener(),
    IVideoPlayer {

    private var ytController: YoutubePlayerController? = null

    override fun onReady(youTubePlayer: YouTubePlayer) {
        ytController =
            YoutubePlayerController(
                customPlayerUi,
                youTubePlayer,
                faithYoutubePlayer
            )

        youTubePlayer.addListener(ytController!!)

        val videoId = faithYoutubePlayer.youtubeVideoDetail.videoId
        youTubePlayer.loadVideo(videoId, 0F)
    }

    /**
     * Sets the max progress for seekbar and the duration label
     */
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        super.onVideoDuration(youTubePlayer, duration)
        if (faithYoutubePlayer.hasSeekbar())
        faithYoutubePlayer.seekBar!!.max = duration.toInt()
        if (faithYoutubePlayer.hasDurationField())
        faithYoutubePlayer.durationField!!.text =
            createTimeLabel(
                duration
            )
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        if (faithYoutubePlayer.hasSeekbar())
        faithYoutubePlayer.seekBar!!.progress = second.toInt()
        if (faithYoutubePlayer.hasCurrentTimeField())
        faithYoutubePlayer.currentTimeField!!.text =
            createTimeLabel(
                second
            )
    }

    override fun playVideo(currentState: VideoPlayerState) {
        ytController!!.playVideo(currentState)
    }

    override fun pauseVideo(currentState: VideoPlayerState) {
        ytController!!.pauseVideo(currentState)
    }

    override fun stopVideo(currentState: VideoPlayerState) {
        ytController!!.stopVideo(currentState)
    }

    override fun seekTo(currentState: VideoPlayerState, time: Float) {
        ytController!!.seekTo(currentState, time)
    }

    override fun setFullScreen() {}
}