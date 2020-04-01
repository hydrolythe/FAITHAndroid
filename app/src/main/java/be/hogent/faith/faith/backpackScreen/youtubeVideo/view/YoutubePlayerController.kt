package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.util.getHighQualityThumbnailUrl
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


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
    private val youtubeVideoDetail: YoutubeVideoDetail
) : IVideoPlayer, AbstractYouTubePlayerListener() {

    private var playerTracker: YouTubePlayerTracker? = null
    private var seekBar: SeekBar? = null
    private var playButton: ImageButton? = null
    private var pauseButton: ImageButton? = null
    private var currentTimeLabel: TextView? = null
    private var durationLabel: TextView? = null
    private var title: TextView? = null
    private var thumbnailImg: ImageView? = null
    private var gradient: ImageView? = null
    private var startScreen: ConstraintLayout? = null
    private var playerContainer: ConstraintLayout? = null
    private var playerOnTouchScreen: ConstraintLayout? = null
    private var seekBarContainer: ConstraintLayout? = null
    //  private var alphaAnim : AlphaAnimation? = null

    private var fullscreen : Boolean = false

    init {
        playerTracker = YouTubePlayerTracker()
        youTubePlayer.addListener(playerTracker!!)

        updateUi(customPlayerUi)
        startListeners()
    }

    private fun updateUi(customPlayerUi: View) {
        playButton = customPlayerUi.findViewById(R.id.play_pause_button)
        pauseButton = customPlayerUi.findViewById(R.id.btn_pause_video)
        title = customPlayerUi.findViewById(R.id.txt_title_player)
        thumbnailImg = customPlayerUi.findViewById(R.id.img_thumbnail_player)
        gradient = customPlayerUi.findViewById(R.id.gradient_blue_player)
        startScreen = customPlayerUi.findViewById(R.id.container_player_pause)
        playerContainer = customPlayerUi.findViewById(R.id.container_youtube_playerview)
        playerOnTouchScreen = customPlayerUi.findViewById(R.id.container_player_touch)

        seekBar = customPlayerUi.findViewById(R.id.player_youtube_seekbar)
        currentTimeLabel = customPlayerUi.findViewById(R.id.video_current_time)
        durationLabel = customPlayerUi.findViewById(R.id.video_duration)
        seekBarContainer = customPlayerUi.findViewById(R.id.container_player_seekbar)

        seekBarContainer!!.visibility = View.GONE
        playerOnTouchScreen!!.visibility = View.GONE
        startScreen!!.visibility = View.GONE

        title!!.text = youtubeVideoDetail.fileName

        /**
         * Thumbnail image from YouTube is the background of the pause screen
         */
        Glide.with(thumbnailImg!!).load(getHighQualityThumbnailUrl(youtubeVideoDetail.videoId)).into(thumbnailImg!!)
    }

    fun startListeners() {
        playButton!!.setOnClickListener {
            playVideo(VideoPlayerState.PAUSED)
        }

        pauseButton!!.setOnClickListener {
            pauseVideo(VideoPlayerState.PLAYING)
        }

        playerContainer!!.setOnClickListener {
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

    override fun setFullScreen() {

    }

}