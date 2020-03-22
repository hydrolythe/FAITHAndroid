package be.hogent.faith.faith.details.photo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewPhotoBinding
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.loadImageIntoView
import org.koin.android.viewmodel.ext.android.viewModel

private const val PHOTO_DETAIL = "The photoDetail to be shown"

class ReviewPhotoFragment : Fragment() {

    companion object {
        fun newInstance(photoDetail: PhotoDetail) =
            ReviewPhotoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(PHOTO_DETAIL, photoDetail)
                }
            }
    }

    private lateinit var binding: FragmentViewPhotoBinding
    private val photoDetailViewModel: ViewPhotoDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadExistingPhotoDetail()
    }

    private fun loadExistingPhotoDetail() {
        val existingDetail = arguments?.getSerializable(PHOTO_DETAIL) as PhotoDetail
        photoDetailViewModel.setDetail(existingDetail)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_photo, container, false)
        binding.photoDetailViewViewModel = photoDetailViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setUpListeners()
    }

    private fun setUpListeners() {
        photoDetailViewModel.detail.observe(this, Observer { detail ->
            loadImageIntoView(
                this.requireContext(),
                detail.file.path,
                binding.imgReviewPhotoImage
            )
        })

        photoDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })

        photoDetailViewModel.cancelClicked.observe(this, Observer {
            activity!!.onBackPressed()
        })
    }
}