package be.hogent.faith.faith.details.video.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewYoutubeVideoBinding
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.faith.details.externalFile.ExternalFileViewModel
import be.hogent.faith.faith.videoplayer.FaithVideoPlayer
import be.hogent.faith.faith.videoplayer.FaithVideoPlayerFragment
import org.koin.android.viewmodel.ext.android.viewModel

private const val VIDEO_DETAIL = "The video to be shown"

class ViewVideoFragment : FaithVideoPlayerFragment() {

    private lateinit var binding: FragmentViewYoutubeVideoBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()
    private var navigation: ViewExternalVideoNavigation? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ViewExternalVideoNavigation) {
            navigation = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_youtube_video,
            container,
            false
        )

        externalFileViewModel.currentFile.observe(viewLifecycleOwner, Observer { file ->
            val faithYoutubePlayer =
                FaithVideoPlayer(
                    playerParentView = binding.cardYoutubePlayer,
                    playButton = binding.btnPlayYtVideo,
                    pauseButton = binding.btnPauseYtVideo,
                    currentTimeField = binding.textCurrentimeYtVideo,
                    durationField = binding.textDurationYtVideo,
                    stopButton = binding.btnStopYtVideo,
                    seekBar = binding.seekbarYtVideo,
                    fullscreenButton = binding.btnFullscreenYtVideo
                )

            setFaithPlayer(faithYoutubePlayer)
            playNewVideo(VideoDetail(file))
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.btnBackYtVideo.setOnClickListener {
            navigation?.backToEvent()
        }

        loadExistingVideo()
    }

    private fun loadExistingVideo() {
        when(val detail = requireArguments().getSerializable(VIDEO_DETAIL)) {
            is VideoDetail ->externalFileViewModel.loadExistingDetail(detail)
            is FilmDetail ->externalFileViewModel.loadExistingDetail(detail)
            else -> throw IllegalArgumentException("Invalid type of detail to play video")
        }
    }

    companion object {
        fun newInstance(filmDetail: FilmDetail): ViewVideoFragment {
            return ViewVideoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(VIDEO_DETAIL, filmDetail)
                }
            }
        }
        fun newInstance(videoDetail: VideoDetail): ViewVideoFragment {
            return ViewVideoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(VIDEO_DETAIL, videoDetail)
                }
            }
        }
    }

    interface ViewExternalVideoNavigation {
        fun backToEvent()
    }
}
