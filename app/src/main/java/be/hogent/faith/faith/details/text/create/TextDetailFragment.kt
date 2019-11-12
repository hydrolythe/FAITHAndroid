package be.hogent.faith.faith.details.text.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEnterTextBinding
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import kotlinx.android.synthetic.main.fragment_enter_text.enterText_editor
import org.koin.android.viewmodel.ext.android.viewModel

// uses https://github.com/wasabeef/richeditor-android
private const val TEXT_DETAIL = "uuid of the text file"

class TextDetailFragment : Fragment(), DetailFragment<TextDetail> {
    override lateinit var detailFinishedListener: DetailFinishedListener

    private val textDetailDetailViewModel: TextDetailViewModel by viewModel()

    private lateinit var enterTextBinding: FragmentEnterTextBinding

    private var navigation: TextScreenNavigation? = null

    companion object {
        fun newInstance(): TextDetailFragment {
            return TextDetailFragment()
        }

        fun newInstance(textDetail: TextDetail): TextDetailFragment {
            return TextDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TEXT_DETAIL, textDetail)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (existingTextGiven()) {
            loadExistingTextDetail()
        }
    }

    private fun loadExistingTextDetail() {
        val existingDetail = arguments!!.getSerializable(TEXT_DETAIL) as TextDetail
        textDetailDetailViewModel.loadExistingDetail(existingDetail)
    }

    private fun existingTextGiven(): Boolean {
        return arguments?.getSerializable(TEXT_DETAIL) != null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        enterTextBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_enter_text, container, false)
        enterTextBinding.textDetailViewModel = textDetailDetailViewModel
        enterTextBinding.lifecycleOwner = this
        return enterTextBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TextScreenNavigation) {
            navigation = context
        }
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
        } else {
            throw AssertionError("A detailFragment has to be started with a DetailFinishedListener")
        }
    }

    override fun onStart() {
        super.onStart()
        initEditor()
        setUpListeners()
    }

    private fun initEditor() {
        with(enterText_editor) {
            setEditorHeight(200)
            setEditorFontSize(30)
            setPadding(10, 10, 10, 10)
            setOnTextChangeListener {
                textDetailDetailViewModel.setText(it)
            }
            // Make sure editor contents are not null so we can save
            focusEditor()
        }
        val inputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(enterText_editor, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setUpListeners() {
        textDetailDetailViewModel.initialText.observe(this, Observer { text ->
            enterText_editor.html = text
        })
        textDetailDetailViewModel.selectedTextColor.observe(this, Observer { newColor ->
            enterText_editor.setTextColor(newColor)
        })
        textDetailDetailViewModel.boldClicked.observe(this, Observer {
            enterText_editor.setBold()
        })
        textDetailDetailViewModel.italicClicked.observe(this, Observer {
            enterText_editor.setItalic()
        })
        textDetailDetailViewModel.underlineClicked.observe(this, Observer {
            enterText_editor.setUnderline()
        })
        textDetailDetailViewModel.selectedFontSize.observe(this, Observer { newSize ->
            enterText_editor.setFontSize(newSize.size)
        })
        textDetailDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceId ->
            Toast.makeText(context, errorMessageResourceId, Toast.LENGTH_SHORT).show()
        })
        textDetailDetailViewModel.savedDetail.observe(this, Observer { savedTextDetail ->
            Toast.makeText(context, R.string.save_text_success, Toast.LENGTH_SHORT).show()
            detailFinishedListener.onDetailFinished(savedTextDetail)
            navigation?.backToEvent()
        })
    }

    interface TextScreenNavigation {
        fun backToEvent()
    }
}