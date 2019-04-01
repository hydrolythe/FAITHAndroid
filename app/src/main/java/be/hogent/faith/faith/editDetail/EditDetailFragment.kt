package be.hogent.faith.faith.editDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment
import be.hogent.faith.faith.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.faith.util.replaceChildFragment


const val ARG_DETAILTYPE = "detailType"

class EditDetailFragment : Fragment() {
    private var detailType: DetailType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detailType = it.getSerializable(ARG_DETAILTYPE) as DetailType
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        replaceChildFragment(DrawEmotionAvatarFragment.newInstance(R.drawable.outline), R.id.fragment_avatar_editdetail)
        when (detailType) {
            DetailType.PICTURE -> replaceChildFragment(
                TakePhotoFragment.newInstance(),
                R.id.fragment_container_editdetail
            )
            DetailType.AUDIO -> replaceChildFragment(
                RecordAudioFragment.newInstance(),
                R.id.fragment_container_editdetail
            )
            else -> Log.e(TAG, "type not defined")
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(detailType: DetailType) =
            EditDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DETAILTYPE, detailType)
                }
            }
    }
}
