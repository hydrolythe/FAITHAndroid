package be.hogent.faith.faith.details.text.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentViewTextBinding
import be.hogent.faith.domain.models.detail.TextDetail
import org.koin.android.viewmodel.ext.android.viewModel

private const val TEXT_DETAIL = "the drawing to be shown"

class ViewTextDetailFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(textDetail: TextDetail) =
            ViewTextDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TEXT_DETAIL, textDetail)
                }
            }
    }

    private lateinit var binding: FragmentViewTextBinding
    private val textDetailViewModel: ViewTextDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadExistingTextDetail()
    }

    private fun loadExistingTextDetail() {
        val existingDetail = arguments?.getSerializable(TEXT_DETAIL) as TextDetail
        textDetailViewModel.setDetail(existingDetail)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_text, container, false)
        binding.textDetailViewModel = textDetailViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initEditor()
        setUpListeners()
    }

    private fun setUpListeners() {
        textDetailViewModel.initialText.observe(this, Observer { text ->
            binding.editorViewText.html = text
        })

        textDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceID ->
            Toast.makeText(context, errorMessageResourceID, Toast.LENGTH_SHORT).show()
        })

        textDetailViewModel.cancelClicked.observe(this, Observer {
            activity!!.onBackPressed()
        })
    }

    private fun initEditor() {
        with(binding.editorViewText) {
            setEditorWidth(dpToPx(binding.cardViewText.width))
            setEditorHeight(dpToPx(binding.cardViewText.height))
            setPadding(10, 10, 10, 10)
            setInputEnabled(false)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return dp * context!!.getResources().getDisplayMetrics().density.toInt()
    }
}
