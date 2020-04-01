package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewYoutubeVideoBinding
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Uses: https://github.com/PierfrancescoSoffritti/android-youtube-player
 * Why? - Customisable lay-out if we use a video player screen
 *      - Ability to disable YouTube buttons
*
* Read more: https://medium.com/@soffritti.pierfrancesco/how-to-play-youtube-videos-in-your-android-app-c40427215230
*/
class ViewYoutubeVideoFragment(private val youtubeVideoDetail: YoutubeVideoDetail) : IVideoPlayer, YouTubePlayerFullScreenListener, Fragment() {

    private lateinit var viewYoutubeVideoBinding: FragmentViewYoutubeVideoBinding
    private lateinit var youTubePlayerView: YouTubePlayerView
    private var navigation: ViewYoutubeVideoNavigation? = null
    private var ytController: YoutubePlayerController? = null
    private val videoPlayerStateViewModel : VideoPlayerStateViewModel by viewModel()
    private var fullscreen : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewYoutubeVideoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_youtube_video, container, false)

        return viewYoutubeVideoBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewYoutubeVideoNavigation) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        youTubePlayerView = viewYoutubeVideoBinding.youtubePlayerView

        lifecycle.addObserver(youTubePlayerView)

        val customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.player_youtube_custom)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                ytController = YoutubePlayerController(
                    customPlayerUi,
                    youTubePlayer,
                    youtubeVideoDetail
                )

                youTubePlayer.addListener(ytController!!)

                val videoId = youtubeVideoDetail.videoId
                youTubePlayer.loadVideo(videoId, 0F)
            }

            /**
             * Sets the max progress for seekbar and the duration label
             */
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                super.onVideoDuration(youTubePlayer, duration)
                viewYoutubeVideoBinding.seekbarYtVideo.max = duration.toInt()
                viewYoutubeVideoBinding.textDurationYtVideo.text = createTimeLabel(duration)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                viewYoutubeVideoBinding.seekbarYtVideo.progress = second.toInt()
                viewYoutubeVideoBinding.textCurrentimeYtVideo.text = createTimeLabel(second)
            }

        })

        viewYoutubeVideoBinding.seekbarYtVideo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                   seekTo(videoPlayerStateViewModel.currentState.value!!, progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        viewYoutubeVideoBinding.btnPlayYtVideo.setOnClickListener {
            videoPlayerStateViewModel.setVideoPlayerState(VideoPlayerState.PLAYING)
        }

        viewYoutubeVideoBinding.btnPauseYtVideo.setOnClickListener {
            videoPlayerStateViewModel.setVideoPlayerState(VideoPlayerState.PAUSED)
        }

        viewYoutubeVideoBinding.btnStopYtVideo.setOnClickListener {
            videoPlayerStateViewModel.setVideoPlayerState(VideoPlayerState.STOPPED)
        }

        viewYoutubeVideoBinding.btnBackYtVideo.setOnClickListener {
            navigation!!.backToEvent()
        }

        viewYoutubeVideoBinding.btnFullscreenYtVideo.setOnClickListener {
            setFullScreen()
        }

        videoPlayerStateViewModel.currentState.observe(this, Observer {
            if (it == VideoPlayerState.PLAYING)
                playVideo(it)
            if (it == VideoPlayerState.PAUSED)
                pauseVideo(it)
            if (it == VideoPlayerState.STOPPED)
                stopVideo(it)
        })
    }

    companion object {
        fun newInstance(youtubeVideoDetail: YoutubeVideoDetail): ViewYoutubeVideoFragment {
            return ViewYoutubeVideoFragment(youtubeVideoDetail)
        }
    }

    /**
     * Behaviour required by Play Store. The audio must be stopped when the video isn't visible.
     */
    override fun onDestroy() {
        super.onDestroy()
        youTubePlayerView.release()
    }

    interface ViewYoutubeVideoNavigation {
        fun backToEvent()
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
        viewYoutubeVideoBinding.textCurrentimeYtVideo.text = createTimeLabel(time)
    }

    override fun setFullScreen() {
        if(fullscreen) {
            youTubePlayerView.exitFullScreen()
            onYouTubePlayerExitFullScreen()
        }
        else{
            youTubePlayerView.enterFullScreen()
            onYouTubePlayerEnterFullScreen()
        }
        fullscreen = !fullscreen
    }

    override fun onYouTubePlayerEnterFullScreen() {
        viewYoutubeVideoBinding.layerFullscreenYtPlayer.visibility = View.VISIBLE
        val viewParams: ViewGroup.LayoutParams = viewYoutubeVideoBinding.cardYoutubePlayer.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        viewYoutubeVideoBinding.cardYoutubePlayer.layoutParams = viewParams
    }

    override fun onYouTubePlayerExitFullScreen() {
        viewYoutubeVideoBinding.layerFullscreenYtPlayer.visibility = View.GONE
        val viewParams: ViewGroup.LayoutParams = viewYoutubeVideoBinding.cardYoutubePlayer.layoutParams
        viewParams.height = 0
        viewParams.width = 0
        viewYoutubeVideoBinding.cardYoutubePlayer.layoutParams = viewParams
    }

    /**
     * Creates a label like this: 2:45
     */
    private fun createTimeLabel(time: Float): String {
        val min: Int = time.toInt() / 60
        val sec: Int = time.toInt() % 60

        var timeLabel = min.toString()
        timeLabel += ":"
        if (sec < 10)
            timeLabel += "0"
        timeLabel += sec
        return timeLabel
    }
}
