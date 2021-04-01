package be.hogent.faith.faith.details.youtubeVideo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewYoutubeVideoBinding
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.videoplayer.FaithVideoPlayer
import be.hogent.faith.faith.videoplayer.FaithVideoPlayerFragment

class ViewYoutubeVideoFragment(private val youtubeVideoDetail: YoutubeVideoDetail) :
    FaithVideoPlayerFragment() {

    private lateinit var viewYoutubeVideoBinding: FragmentViewYoutubeVideoBinding
    private var navigation: ViewYoutubeVideoNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewYoutubeVideoBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_youtube_video,
            container,
            false
        )

        setFaithPlayer(
            FaithVideoPlayer(
                playerParentView = viewYoutubeVideoBinding.cardYoutubePlayer,
                playButton = viewYoutubeVideoBinding.btnPlayYtVideo,
                pauseButton = viewYoutubeVideoBinding.btnPauseYtVideo,
                currentTimeField = viewYoutubeVideoBinding.textCurrentimeYtVideo,
                durationField = viewYoutubeVideoBinding.textDurationYtVideo,
                stopButton = viewYoutubeVideoBinding.btnStopYtVideo,
                seekBar = viewYoutubeVideoBinding.seekbarYtVideo,
                fullscreenButton = viewYoutubeVideoBinding.btnFullscreenYtVideo
            )
        )

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
        playNewVideo(youtubeVideoDetail)

        viewYoutubeVideoBinding.btnBackYtVideo.setOnClickListener {
            navigation!!.backToEvent()
        }
    }

    companion object {
        fun newInstance(youtubeVideoDetail: YoutubeVideoDetail): ViewYoutubeVideoFragment {
            return ViewYoutubeVideoFragment(
                youtubeVideoDetail
            )
        }
    }

    interface ViewYoutubeVideoNavigation {
        fun backToEvent()
    }
}
