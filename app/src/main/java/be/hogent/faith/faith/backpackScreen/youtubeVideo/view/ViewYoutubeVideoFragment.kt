package be.hogent.faith.faith.backpackScreen.youtubeVideo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewYoutubeVideoBinding
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.FaithYoutubePlayer
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.FaithYoutubePlayerFragment
import be.hogent.faith.faith.backpackScreen.youtubeVideo.player.FaithYoutubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 * extends abstract FaithYoutubePlayer fragment
 */
class ViewYoutubeVideoFragment(private val youtubeVideoDetail: YoutubeVideoDetail) : FaithYoutubePlayerFragment() {

    private lateinit var viewYoutubeVideoBinding: FragmentViewYoutubeVideoBinding
    private var navigation: ViewYoutubeVideoNavigation? = null
    private var youtubePlayerView: YouTubePlayerView? = null

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

    /**
     * Everything you need to play a new video in your fragment
     */
    override fun onStart() {
        super.onStart()

        youtubePlayerView = viewYoutubeVideoBinding.youtubePlayerView

        val faithYoutubePlayer =
            FaithYoutubePlayer(
                youtubeVideoDetail = youtubeVideoDetail,
                youtubePlayerView = youtubePlayerView!!,
                playerParentView = viewYoutubeVideoBinding.cardYoutubePlayer,
                playButton = viewYoutubeVideoBinding.btnPlayYtVideo as ImageButton,
                pauseButton = viewYoutubeVideoBinding.btnPauseYtVideo as ImageButton
            )

        faithYoutubePlayer.currentTimeField = viewYoutubeVideoBinding.textCurrentimeYtVideo as TextView
        faithYoutubePlayer.durationField = viewYoutubeVideoBinding.textDurationYtVideo as TextView
        faithYoutubePlayer.seekBar = viewYoutubeVideoBinding.seekbarYtVideo as SeekBar
        faithYoutubePlayer.stopButton = viewYoutubeVideoBinding.btnStopYtVideo as ImageButton
        faithYoutubePlayer.fullscreenButton = viewYoutubeVideoBinding.btnFullscreenYtVideo as ImageButton
        faithYoutubePlayer.timeContainer = viewYoutubeVideoBinding.timeContainerYtVideo
        faithYoutubePlayer.menuContainer = viewYoutubeVideoBinding.menuContainerYtVideo

        setFaithYoutubePlayer(faithYoutubePlayer)

        viewYoutubeVideoBinding.btnBackYtVideo.setOnClickListener {
            navigation!!.backToEvent()
        }
    }

    override fun onYouTubePlayerEnterFullScreen() {
        super.onYouTubePlayerEnterFullScreen()
        viewYoutubeVideoBinding.layerFullscreenYtPlayer.visibility = View.VISIBLE
    }

    override fun onYouTubePlayerExitFullScreen() {
        super.onYouTubePlayerExitFullScreen()
        viewYoutubeVideoBinding.layerFullscreenYtPlayer.visibility = View.GONE
    }

    companion object {
        fun newInstance(youtubeVideoDetail: YoutubeVideoDetail): ViewYoutubeVideoFragment {
            return ViewYoutubeVideoFragment(youtubeVideoDetail)
        }
    }

    interface ViewYoutubeVideoNavigation {
        fun backToEvent()
    }
}
