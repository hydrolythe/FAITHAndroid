package be.hogent.faith.faith.details.externalVideo.view

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewExternalVideoBinding
import be.hogent.faith.domain.models.detail.ExternalVideoDetail

private const val VIDEO_DETAIL = "The video to be shown"

class ViewExternalVideoFragment : Fragment() {

    private lateinit var binding: FragmentViewExternalVideoBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        loadVideoFromDetail()
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_view_external_video, container, false)
        return binding.root
    }


    private fun loadVideoFromDetail(){
        val externalVideoDetail = arguments?.getSerializable(VIDEO_DETAIL) as ExternalVideoDetail
        binding.video.setVideoURI(Uri.fromFile(externalVideoDetail.file))
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
