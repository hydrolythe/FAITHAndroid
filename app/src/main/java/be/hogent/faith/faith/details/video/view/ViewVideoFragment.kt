package be.hogent.faith.faith.details.video.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewVideoBinding
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.faith.details.externalFile.ExternalFileViewModel
import org.koin.android.viewmodel.ext.android.viewModel

private const val VIDEO_DETAIL = "The video to be shown"

class ViewVideoFragment : Fragment() {

    private lateinit var binding: FragmentViewVideoBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_video, container, false)

        startListeners()

        loadExistingVideo()

        return binding.root
    }

    private fun startListeners() {
        externalFileViewModel.currentFile.observe(viewLifecycleOwner, Observer { file ->
            binding.video.setVideoURI(Uri.fromFile(file))
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(binding.video)
            binding.video.setMediaController(mediaController)
        })
    }

    private fun loadExistingVideo() {
        val externalVideoDetail =
            requireArguments().getSerializable(VIDEO_DETAIL) as VideoDetail
        externalFileViewModel.loadExistingDetail(externalVideoDetail)
    }

    companion object {
        fun newInstance(videoDetail: VideoDetail): ViewVideoFragment {
            return ViewVideoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(VIDEO_DETAIL, videoDetail)
                }
            }
        }
    }
}
