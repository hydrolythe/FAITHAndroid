package be.hogent.faith.faith.emotionCapture.enterText

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
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import kotlinx.android.synthetic.main.fragment_enter_text.enterText_editor
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.UUID

// uses https://github.com/wasabeef/richeditor-android
private const val TEXT_DETAIL_UUID = "uuid of the text file"

class EnterTextFragment : Fragment() {

    private val enterTextViewModel: EnterTextViewModel by viewModel()
    private val eventViewModel: EventViewModel by sharedViewModel()

    private lateinit var enterTextBinding: FragmentEnterTextBinding

    private var navigation: TextScreenNavigation? = null

    companion object {
        fun newInstance(): EnterTextFragment {
            return EnterTextFragment()
        }

        fun newInstance(detailUuid: UUID): EnterTextFragment {
            return EnterTextFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TEXT_DETAIL_UUID, detailUuid)
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
        val detailUuid = arguments?.getSerializable(TEXT_DETAIL_UUID) as UUID
        val givenDetail = eventViewModel.event.value!!.details.find { it.uuid === detailUuid }
        if (givenDetail is TextDetail) {
            enterTextViewModel.loadExistingTextDetail(givenDetail)
        } else {
            throw IllegalArgumentException("Got a Detail that wasn't a TextDetail!")
        }
    }

    private fun existingTextGiven(): Boolean {
        return arguments?.getSerializable(TEXT_DETAIL_UUID) != null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        enterTextBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_enter_text, container, false)
        enterTextBinding.enterTextViewModel = enterTextViewModel
        enterTextBinding.eventViewModel = eventViewModel
        enterTextBinding.lifecycleOwner = this
        return enterTextBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TextScreenNavigation) {
            navigation = context
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
                enterTextViewModel.setText(it)
            }
            focusEditor()
        }
        val inputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(enterText_editor, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setUpListeners() {
        enterTextViewModel.text.observe(this, Observer { text ->
            enterText_editor.html = text
        })
        enterTextViewModel.selectedTextColor.observe(this, Observer { newColor ->
            enterText_editor.setTextColor(newColor)
        })
        enterTextViewModel.boldClicked.observe(this, Observer {
            enterText_editor.setBold()
        })
        enterTextViewModel.italicClicked.observe(this, Observer {
            enterText_editor.setItalic()
        })
        enterTextViewModel.underlineClicked.observe(this, Observer {
            enterText_editor.setUnderline()
        })
        enterTextViewModel.selectedFontSize.observe(this, Observer { newSize ->
            enterText_editor.setFontSize(newSize.size)
        })
        eventViewModel.errorMessage.observe(this, Observer { errorMessageResourceId ->
            Toast.makeText(context, errorMessageResourceId, Toast.LENGTH_SHORT).show()
        })
        eventViewModel.textSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_text_success, Toast.LENGTH_SHORT).show()
            navigation?.backToEvent()
        })
    }

    interface TextScreenNavigation {
        fun backToEvent()
    }
}