package be.hogent.faith.faith.details.photo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentReviewPhotoBinding
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.util.UUID

private const val PHOTO_DETAIL_UUID = "UUID of the photoDetail to be shown"

class ReviewPhotoFragment : Fragment() {
    private val eventViewModel: EventViewModel by sharedViewModel()

    companion object {
        fun newInstance(detailUuid: UUID): ReviewPhotoFragment {
            val args = Bundle().apply {
                putSerializable(PHOTO_DETAIL_UUID, detailUuid)
            }
            return ReviewPhotoFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var binding: FragmentReviewPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_review_photo, container, false)

        val photoFile = getPhotoFileFromArguments()
        Glide.with(requireContext())
            .load(photoFile)
            .into(binding.imgReviewPhotoImage)

        return binding.root
    }

    private fun getPhotoFileFromArguments(): File {
        val detailUuid = arguments?.getSerializable(PHOTO_DETAIL_UUID) as UUID
        val givenDetail = eventViewModel.event.value!!.details.find { it.uuid === detailUuid }
        if (givenDetail is PhotoDetail) {
            return givenDetail.file
        } else {
            throw IllegalArgumentException("Given UUID was not from a PhotoDetail")
        }
    }
}