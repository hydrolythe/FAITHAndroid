package be.hogent.faith.faith.details.video.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.VideoDetail

private const val VIDEO_DETAIL = "uuid of the video file"

class ViewVideoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_video, container, false)
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
