package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

import android.view.View
import android.widget.Chronometer
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

/**
 * Custom YoutubePlayer UI controller
 * Used to customize our player and make it fit our lay-outs
 * Written to make sure we have complete control over each player state
 * Consists of 3 screens: empty player screen, on touch screen with pause button and a pause screen
 * It's easy to change the screens and lay-out
 */
class YoutubePlayerController(
    customPlayerUi: View,
    private val youTubePlayer: YouTubePlayer,
    private val youtubeVideoDetail: YoutubeVideoDetail
) : AbstractYouTubePlayerListener() {

    private var playerTracker: YouTubePlayerTracker? = null
    private var seekBar: SeekBar? = null
    private var playButton: ImageButton? = null
    private var pauseButton: ImageButton? = null
    private var stopButton: ImageButton? = null
    private var chronometer: Chronometer? = null
    private var currentTimeLabel: TextView? = null
    private var durationLabel: TextView? = null
    private var title: TextView? = null
    private var thumbnailImg: ImageView? = null
    private var gradient: ImageView? = null
    private var pauseScreen: ConstraintLayout? = null
    private var playerContainer: ConstraintLayout? = null
    private var playerOnTouchScreen: ConstraintLayout? = null
    private var seekBarContainer: ConstraintLayout? = null
    private var max = 0F
    //  private var alphaAnim : AlphaAnimation? = null

    private var currentState: YoutubePlayerState = YoutubePlayerState.PLAYER_SCREEN

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
        pauseScreen = customPlayerUi.findViewById(R.id.container_player_pause)
        playerContainer = customPlayerUi.findViewById(R.id.container_youtube_playerview)
        playerOnTouchScreen = customPlayerUi.findViewById(R.id.container_player_touch)
        stopButton = customPlayerUi.findViewById(R.id.btn_stop_video)

        seekBar = customPlayerUi.findViewById(R.id.player_youtube_seekbar)
        currentTimeLabel = customPlayerUi.findViewById(R.id.video_current_time)
        chronometer = customPlayerUi.findViewById(R.id.video_current_time_chrono)
        durationLabel = customPlayerUi.findViewById(R.id.video_duration)
        seekBarContainer = customPlayerUi.findViewById(R.id.container_player_seekbar)

        title!!.text = youtubeVideoDetail.fileName

        /**
         * Thumbnail image from YouTube is the background of the pause screen
         */
        Glide.with(thumbnailImg!!).load(getHighQualityThumbnailUrl(youtubeVideoDetail.videoId)).into(thumbnailImg!!)
    }

    fun startListeners() {
        playButton!!.setOnClickListener {
            youTubePlayer.play()
            viewPlayerScreen()
        }

        pauseButton!!.setOnClickListener {
            //  alphaAnim!!.cancel()
            youTubePlayer.pause()
            viewPauseMenu()
        }

        /**
         * Stop = when the video ends to avoid seeing the "watch more videos" screen
         */
        stopButton!!.setOnClickListener {
            youTubePlayer.seekTo(max)
        }

        /**
         * When the playerscreen is touched the seekbar, back button and pause button are shown for 5 seconds
         */
        playerContainer!!.setOnClickListener {
            if (currentState != YoutubePlayerState.PAUSE_MENU_SEEKING || currentState != YoutubePlayerState.PAUSE_MENU) {
                if (currentState == YoutubePlayerState.PLAYER_SCREEN) {
                    viewPlayerScreenWithOptions()
                } else if (currentState == YoutubePlayerState.PLAYER_SCREEN_OPTIONS &&
                    currentState != YoutubePlayerState.PLAYER_SCREEN_SEEKING && currentState != YoutubePlayerState.PAUSE_MENU_SEEKING) {
                    playerOnTouchScreen!!.visibility = View.GONE
                    seekBarContainer!!.visibility = View.GONE
                    currentState = YoutubePlayerState.PLAYER_SCREEN
                }
            }
        }

        seekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBarContainer!!.visibility = View.VISIBLE
                    youTubePlayer.seekTo(progress.toFloat())
                    currentTimeLabel!!.text = createTimeLabel(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                startSeeking()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                stopSeeking()
            }
        })
    }

    /**
     * Updates current position seekbar and timelabel value
     */
    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        super.onCurrentSecond(youTubePlayer, second)
        seekBar!!.progress = second.toInt()
        currentTimeLabel!!.text = createTimeLabel(second.toInt())
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        super.onStateChange(youTubePlayer, state)
        if (state == PlayerConstants.PlayerState.PAUSED) {
            //  if(alphaAnim != null)
            //   alphaAnim!!.cancel()
            viewPauseMenu()
        } else if (state == PlayerConstants.PlayerState.BUFFERING) {
            viewPlayerScreen()
        }
    }

    /**
     * Empty player screen
     */
    private fun viewPlayerScreen() {
        currentState = YoutubePlayerState.PLAYER_SCREEN

        playerOnTouchScreen!!.visibility = View.GONE
        seekBarContainer!!.visibility = View.GONE
        pauseScreen!!.visibility = View.GONE
    }

    /**
     * A screen with pause button, seekbar and back button appears on player screen clicked
     */
    private fun viewPlayerScreenWithOptions() {
        currentState = YoutubePlayerState.PLAYER_SCREEN_OPTIONS

        playerOnTouchScreen!!.visibility = View.VISIBLE
        seekBarContainer!!.visibility = View.VISIBLE
        pauseScreen!!.visibility = View.GONE

        // TODO add animation when everything else is working
/*
        alphaAnim = AlphaAnimation(1.0f, 0.0f)
        alphaAnim!!.startOffset = 3000
        alphaAnim!!.duration = 400
        alphaAnim!!.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}

            override fun onAnimationEnd(animation: Animation) {
               viewPlayerScreen()
            }

            override fun onAnimationStart(p0: Animation?) {}
        })

        playerOnTouchScreen!!.animation = alphaAnim*/
    }

    /**
     * Screen that's when in pause state consisting of video thumbnail as background, seek bar and play button
     */
    private fun viewPauseMenu() {
        currentState = YoutubePlayerState.PAUSE_MENU

    //    alphaAnim!!.cancel()

        playerOnTouchScreen!!.visibility = View.GONE
        pauseScreen!!.visibility = View.VISIBLE
        seekBarContainer!!.visibility = View.VISIBLE
    }

    /**
     * When seeking in pause state a current video preview is shown
     */
    private fun startSeeking() {
        seekBarContainer!!.visibility = View.VISIBLE

        if (currentState == YoutubePlayerState.PAUSE_MENU)
            currentState = YoutubePlayerState.PAUSE_MENU_SEEKING
        else {
            currentState = YoutubePlayerState.PLAYER_SCREEN_SEEKING
            //  alphaAnim!!.cancel()
        }
        playerOnTouchScreen!!.visibility = View.GONE
        pauseScreen!!.visibility = View.GONE
        seekBarContainer!!.visibility = View.VISIBLE
    }

    private fun stopSeeking() {
        if (currentState == YoutubePlayerState.PAUSE_MENU_SEEKING) {
            playerOnTouchScreen!!.visibility = View.GONE
            pauseScreen!!.visibility = View.VISIBLE
        } else if (currentState == YoutubePlayerState.PLAYER_SCREEN_SEEKING) {
            playerOnTouchScreen!!.visibility = View.VISIBLE
        }
        seekBarContainer!!.visibility = View.VISIBLE
    }

    /**
     * Sets the max progress for seekbar and the duration label
     */
    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        super.onVideoDuration(youTubePlayer, duration)
        seekBar!!.max = duration.toInt()
        max = duration
        durationLabel!!.text = createTimeLabel(duration.toInt())
    }

    /**
     * Creates a label like this: 2:45
     */
    private fun createTimeLabel(time: Int): String {
        val min: Int = time / 60
        val sec: Int = time % 60

        var timeLabel = min.toString()
        timeLabel += ":"
        if (sec < 10)
            timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }
}

enum class YoutubePlayerState {
    PLAYER_SCREEN, PLAYER_SCREEN_OPTIONS, PLAYER_SCREEN_SEEKING, PAUSE_MENU, PAUSE_MENU_SEEKING
}