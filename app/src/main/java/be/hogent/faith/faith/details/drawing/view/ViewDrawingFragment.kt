package be.hogent.faith.faith.details.drawing.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewDrawingBinding
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.loadImageIntoView
import org.koin.android.viewmodel.ext.android.viewModel

private const val DRAWING_DETAIL = "the drawing to be shown"

class ViewDrawingFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(drawingDetail: DrawingDetail) =
            ViewDrawingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(DRAWING_DETAIL, drawingDetail)
                }
            }
    }

    private lateinit var binding: FragmentViewDrawingBinding
    private val drawingDetailViewModel: ViewDrawingDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadExistingDrawingDetail()
    }

    private fun loadExistingDrawingDetail() {
        val existingDetail = arguments?.getSerializable(DRAWING_DETAIL) as DrawingDetail
        drawingDetailViewModel.setDetail(existingDetail)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_drawing, container, false)
        binding.drawingDetailViewViewModel = drawingDetailViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setUpListeners()
    }

    private fun setUpListeners() {
        drawingDetailViewModel.detail.observe(this, Observer { detail ->
            loadImageIntoView(
                this.requireContext(),
                detail.file.path,
                binding.imgViewDrawingImage
            )
        })

        drawingDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })

        drawingDetailViewModel.cancelClicked.observe(this, Observer {
            activity!!.onBackPressed()
        })
    }
}
