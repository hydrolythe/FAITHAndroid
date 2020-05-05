package be.hogent.faith.faith.videoplayer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import com.google.android.material.card.MaterialCardView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.lang.UnsupportedOperationException

class FaithVideoPlayer(
    private val playerParentView: MaterialCardView,
    private val playButton: View,
    private val pauseButton: View,
    private var isFullscreen: Boolean = false,
    private var seekBar: SeekBar? = null,
    private var currentTimeField: TextView? = null,
    private var durationField: TextView? = null,
    private var stopButton: View? = null,
    private var fullscreenButton: View? = null
) : IVideoPlayer {

    private var controller: IVideoPlayer? = null

    init {
        initPlayerListeners()
    }

    private fun initPlayerListeners() {
        playButton.setOnClickListener {
            playVideo()
        }

        pauseButton.setOnClickListener {
            pauseVideo()
        }

        stopButton?.setOnClickListener {
                stopVideo()
            }

        fullscreenButton?.setOnClickListener {
                setFullScreen()
            }

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekTo(progress.toFloat())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    fun playNewVideo(detail: Detail, context: Context) {
        playerParentView.removeAllViews()
        when (detail) {
            is YoutubeVideoDetail -> playYoutubeVideo(detail, context)
            is FilmDetail -> playVideoFile(detail, context)
            is ExternalVideoDetail -> playVideoFile(detail, context)
            else -> throw UnsupportedOperationException("This file cannot be played")
        }
    }

    private fun playYoutubeVideo(detail: Detail, context: Context){
        val youTubePlayerView = YouTubePlayerView(context)
        controller = YoutubePlayerController(this, youTubePlayerView, detail as YoutubeVideoDetail)
        playerParentView.addView(youTubePlayerView)
    }

    private fun playVideoFile(detail: Detail, context: Context){
        val videoView = VideoView(context)
        controller = VideoFilePlayerController(this, videoView, detail.file)
        playerParentView.addView(videoView)
    }

    fun setDurationVideo(duration: Float) {
        seekBar?.max = duration.toInt()
        durationField?.text = createTimeLabel(duration)
    }

    fun setCurrentTimeVideo(second: Float) {
        seekBar?.progress = second.toInt()
        currentTimeField?.text = createTimeLabel(second)
    }

    override fun playVideo() {
        controller!!.playVideo()
    }

    override fun resumeVideo() {
        controller!!.resumeVideo()
    }

    override fun pauseVideo() {
        controller!!.pauseVideo()
    }

    override fun stopVideo() {
        controller!!.stopVideo()
        seekBar?.progress = 0
        currentTimeField?.text = "0:00"
    }

    override fun seekTo(time: Float) {
        controller!!.seekTo(time)
        currentTimeField?.text = createTimeLabel(time)
    }

    override fun stopPlayer() {
        controller!!.stopPlayer()
    }

    private fun setFullScreen() {
        if (isFullscreen)
            onPlayerExitFullScreen()
        else
            onPlayerEnterFullScreen()
        isFullscreen = !isFullscreen
    }

    private fun onPlayerEnterFullScreen() {
        val viewParams: ViewGroup.LayoutParams = playerParentView.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        playerParentView.layoutParams = viewParams
        playerParentView.radius = 0F
    }

    private fun onPlayerExitFullScreen() {
        val viewParams: ViewGroup.LayoutParams = playerParentView.layoutParams
        viewParams.height = 0
        viewParams.width = 0
        playerParentView.layoutParams = viewParams
        playerParentView.radius = 35F
    }
}