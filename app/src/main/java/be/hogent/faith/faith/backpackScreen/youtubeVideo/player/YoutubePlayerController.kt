package be.hogent.faith.faith.backpackScreen.youtubeVideo.player

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import be.hogent.faith.R
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.FaithYoutubePlayer
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.IVideoPlayer
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.VideoPlayerState
import be.hogent.faith.faith.util.getHighQualityThumbnailUrl
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker


/**
 * Custom YoutubePlayer UI controller
 * Used to customize our player and make it fit our lay-outs
 * Written to make sure we have complete control over each player state
 * Consists of 3 screens: empty player screen, on touch screen with pause button and a pause screen
 * It's easy to change the screens and lay-out
 */
class YoutubePlayerController (
    private val customPlayerUi: View,
    private val youTubePlayer: YouTubePlayer,
    private val faithYoutubePlayer: FaithYoutubePlayer
) : IVideoPlayer, AbstractYouTubePlayerListener() {

    private var playerTracker: YouTubePlayerTracker? = null
    private var title: TextView? = null
    private var thumbnailImg: ImageView? = null
    private var gradient: ImageView? = null
    private var startScreen: ConstraintLayout? = null

    init {
        playerTracker = YouTubePlayerTracker()
        youTubePlayer.addListener(playerTracker!!)

        createCustomUiLayerAbovePlayerView(customPlayerUi)
    }

    private fun createCustomUiLayerAbovePlayerView(customPlayerUi: View) {
        title = customPlayerUi.findViewById(R.id.txt_title_player)
        thumbnailImg = customPlayerUi.findViewById(R.id.img_thumbnail_player)
        gradient = customPlayerUi.findViewById(R.id.gradient_blue_player)
        startScreen = customPlayerUi.findViewById(R.id.container_player_pause)

        title!!.text = faithYoutubePlayer.youtubeVideoDetail.fileName

        /**
         * Thumbnail image from YouTube is the background of the stop screen
         */
        Glide.with(thumbnailImg!!).load(getHighQualityThumbnailUrl(faithYoutubePlayer.youtubeVideoDetail.videoId)).into(thumbnailImg!!)
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        super.onStateChange(youTubePlayer, state)
        if(state == PlayerConstants.PlayerState.BUFFERING)
            showLoadingScreen()
        if(state == PlayerConstants.PlayerState.UNSTARTED)
            showStartScreen()
        if(state == PlayerConstants.PlayerState.PLAYING){
            startScreen!!.visibility = View.GONE
        }
        if(state == PlayerConstants.PlayerState.ENDED)
            showStartScreen()
    }

    private fun showLoadingScreen(){
        //TODO
    }

    private fun showStartScreen(){
        startScreen!!.visibility = View.VISIBLE
    }

    override fun playVideo(currentState: VideoPlayerState) {
        startScreen!!.visibility = View.GONE
        youTubePlayer.play()
    }

    override fun pauseVideo(currentState: VideoPlayerState) {
        youTubePlayer.pause()
    }

    override fun stopVideo(currentState: VideoPlayerState) {
        youTubePlayer.seekTo(0F)
        youTubePlayer.pause()
        showStartScreen()
    }

    override fun seekTo(currentState: VideoPlayerState, time: Float) {
        youTubePlayer.seekTo(time)

        if(currentState == VideoPlayerState.PLAYING)
            youTubePlayer.play()
    }

    override fun setFullScreen() {}
}