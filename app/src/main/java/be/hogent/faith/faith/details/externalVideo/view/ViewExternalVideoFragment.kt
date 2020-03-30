package be.hogent.faith.faith.details.externalVideo.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewExternalVideoBinding
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.faith.backpackScreen.externalFile.ExternalFileViewModel
import org.koin.android.viewmodel.ext.android.viewModel

private const val VIDEO_DETAIL = "The video to be shown"

class ViewExternalVideoFragment : Fragment() {

    private lateinit var binding: FragmentViewExternalVideoBinding
    private val externalFileViewModel: ExternalFileViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_external_video, container, false)
        externalFileViewModel.currentFile.observe(this, Observer { file ->
            binding.video.setVideoURI(Uri.fromFile(file))
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(binding.video)
            binding.video.setMediaController(mediaController)
        })
        loadExistingVideo()

        return binding.root
    }

    private fun loadExistingVideo() {
        val externalVideoDetail = arguments!!.getSerializable(VIDEO_DETAIL) as ExternalVideoDetail
        externalFileViewModel.loadExistingDetail(externalVideoDetail)
    }
    companion object {
        fun newInstance(videoDetail: ExternalVideoDetail): ViewExternalVideoFragment {
            return ViewExternalVideoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(VIDEO_DETAIL, videoDetail)
                }
            }
        }
    }
}
