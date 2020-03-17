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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

private const val PHOTO_DETAIL = "The photoDetail to be shown"

class ReviewPhotoFragment : Fragment() {

    companion object {
        fun newInstance(photoDetail: PhotoDetail): ReviewPhotoFragment {
            val args = Bundle().apply {
                putSerializable(PHOTO_DETAIL, photoDetail)
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

        loadPhotoFromDetail()

        return binding.root
    }

    private fun loadPhotoFromDetail() {
        val photoDetail = arguments?.getSerializable(PHOTO_DETAIL) as PhotoDetail
        Glide.with(requireContext())
            .load(photoDetail.file)
            .into(binding.imgReviewPhotoImage)
    }
}