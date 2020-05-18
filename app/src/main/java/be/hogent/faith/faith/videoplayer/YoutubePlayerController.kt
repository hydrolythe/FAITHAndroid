package be.hogent.faith.faith.videoplayer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.util.getHighQualityThumbnailUrl
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
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
/** Youtube player:
 * Uses: https://github.com/PierfrancescoSoffritti/android-youtube-player
 * Why? - Customisable lay-out if we use a video player screen
 *      - Ability to disable YouTube buttons
 *
 * Read more: https://medium.com/@soffritti.pierfrancesco/how-to-play-youtube-videos-in-your-android-app-c40427215230
 */

class YoutubePlayerController(
    private val youTubePlayerView: YouTubePlayerView,
    private val detail: YoutubeVideoDetail
) : IVideoPlayer, AbstractYouTubePlayerListener() {

    private var playerTracker: YouTubePlayerTracker? = null
    private var title: TextView? = null
    private var thumbnailImg: ImageView? = null
    private var gradient: ImageView? = null
    private var startScreen: ConstraintLayout? = null
    private var customPlayerContainer: ConstraintLayout? = null
    private var customPlayerUi: View? = null
    private var youTubePlayer: YouTubePlayer? = null
    private var currentSecond : Float = 0F
    private var duration : Float = 0F

    init {
        youTubePlayerView.addYouTubePlayerListener(this)
        playerTracker = YouTubePlayerTracker()
        customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.player_youtube_custom)
        createCustomUiLayerAbovePlayerView(customPlayerUi!!)
        startScreen!!.visibility = View.VISIBLE
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        youTubePlayer.addListener(playerTracker!!)

        val videoId = detail.videoId
        youTubePlayer.loadVideo(videoId, 0F)

        this.youTubePlayer = youTubePlayer

        startScreen!!.visibility = View.GONE
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        super.onVideoDuration(youTubePlayer, duration)
        this.duration = duration
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        currentSecond = second
    }

    private fun createCustomUiLayerAbovePlayerView(customPlayerUi: View) {
        title = customPlayerUi.txt_title_player
        thumbnailImg = customPlayerUi.img_thumbnail_player
        gradient = customPlayerUi.gradient_blue_player
        startScreen = customPlayerUi.container_player_pause
        customPlayerContainer = customPlayerUi.container_youtube_playerview

        title!!.text = detail.title

        /**
         * Thumbnail image from YouTube is the background of the stop screen
         */
        Glide.with(thumbnailImg!!).load(getHighQualityThumbnailUrl(detail.videoId)).into(thumbnailImg!!)

        initFullScreenOnClickListener()
    }

    /**
     * Very useful because it prevents users from clicking on default YouTube buttons , instead our menu and seekbar appear
     * It's a layer between our UI and the default YouTube UI
     */
    private fun initFullScreenOnClickListener() {
        customPlayerContainer!!.setOnClickListener {}
    }

    override fun playVideo() {
        startScreen!!.visibility = View.GONE
        youTubePlayer!!.play()
    }

    override fun resumeVideo() {
        youTubePlayer!!.play()
    }

    override fun pauseVideo() {
        youTubePlayer!!.pause()
    }

    override fun stopVideo() {
        youTubePlayer!!.seekTo(0F)
        youTubePlayer!!.pause()
        startScreen!!.visibility = View.VISIBLE
    }

    override fun seekTo(time: Float) {
        youTubePlayer!!.seekTo(time)
    }

    override fun stopPlayer() {
        youTubePlayerView.release()
    }

    override fun getCurrentPosition(): Float {
        return currentSecond
    }

    override fun getDuration(): Float {
        return duration
    }
}