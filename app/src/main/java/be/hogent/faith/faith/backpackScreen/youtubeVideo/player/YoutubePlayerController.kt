package be.hogent.faith.faith.backpackScreen.youtubeVideo.player

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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
import kotlinx.android.synthetic.main.player_youtube_custom.view.container_player_pause
import kotlinx.android.synthetic.main.player_youtube_custom.view.container_youtube_playerview
import kotlinx.android.synthetic.main.player_youtube_custom.view.gradient_blue_player
import kotlinx.android.synthetic.main.player_youtube_custom.view.img_thumbnail_player
import kotlinx.android.synthetic.main.player_youtube_custom.view.txt_title_player


/**
 * Custom YoutubePlayer UI controller
 * Used to customize our player and make it fit our lay-outs
 * Written to make sure we have complete control over each player state
 * Consists of 3 screens: empty player screen, on touch screen with pause button and a pause screen
 * It's easy to change the screens and lay-out
 *
 * The controller puts a custom lay-out on top of the youtubeplayerview declared in the xml files
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
    private var alphaAnim : AlphaAnimation? = null
    private var customPlayerContainer : ConstraintLayout? = null

    init {
        playerTracker = YouTubePlayerTracker()
        youTubePlayer.addListener(playerTracker!!)

        createCustomUiLayerAbovePlayerView(customPlayerUi)
    }

    private fun createCustomUiLayerAbovePlayerView(customPlayerUi: View) {
        title = customPlayerUi.txt_title_player
        thumbnailImg = customPlayerUi.img_thumbnail_player
        gradient = customPlayerUi.gradient_blue_player
        startScreen = customPlayerUi.container_player_pause
        customPlayerContainer = customPlayerUi.container_youtube_playerview

        title!!.text = faithYoutubePlayer.youtubeVideoDetail.title

        /**
         * Thumbnail image from YouTube is the background of the stop screen
         */
        Glide.with(thumbnailImg!!).load(getHighQualityThumbnailUrl(faithYoutubePlayer.youtubeVideoDetail.videoId)).into(thumbnailImg!!)

        initFullScreenOnClickListener()
    }

    /**
     * Very useful because it prevents users from clicking on default YouTube buttons , instead our menu and seekbar appear
     * It's a layer between our UI and the default YouTube UI
     */
    private fun initFullScreenOnClickListener(){
        customPlayerContainer!!.setOnClickListener {
            if(faithYoutubePlayer.isFullscreen){
                startAnimation()
            }
            else
                cancelAnim()
        }

        faithYoutubePlayer.timeContainer!!.setOnClickListener {
            if(faithYoutubePlayer.isFullscreen)
                cancelAnim()
        }

        faithYoutubePlayer.menuContainer!!.setOnClickListener {
            if(faithYoutubePlayer.isFullscreen)
                cancelAnim()
        }
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
        if(state == PlayerConstants.PlayerState.PAUSED){
            if(alphaAnim != null)
                cancelAnim()
        }
        if(state == PlayerConstants.PlayerState.ENDED)
            showStartScreen()
    }

    private fun showLoadingScreen(){
        //TODO
    }

    private fun startAnimation(){
        faithYoutubePlayer.menuContainer!!.visibility = View.VISIBLE
        faithYoutubePlayer.timeContainer!!.visibility = View.VISIBLE

        alphaAnim = AlphaAnimation(1.0f, 0.0f)
        alphaAnim!!.startOffset = 3000
        alphaAnim!!.duration = 400
        alphaAnim!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(animation: Animation) {
                faithYoutubePlayer.menuContainer!!.visibility = View.GONE
                faithYoutubePlayer.timeContainer!!.visibility = View.GONE
            }

            override fun onAnimationStart(p0: Animation?) {}
        })
    }

    private fun cancelAnim(){
        alphaAnim?.cancel()
        alphaAnim = null
        faithYoutubePlayer.menuContainer!!.visibility = View.VISIBLE
        faithYoutubePlayer.timeContainer!!.visibility = View.VISIBLE
    }

    private fun showStartScreen(){
        cancelAnim()
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