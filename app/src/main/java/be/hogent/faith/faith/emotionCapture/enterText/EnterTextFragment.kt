package be.hogent.faith.faith.emotionCapture.enterText

// uses https://github.com/wasabeef/richeditor-android

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.util.TAG
import kotlinx.android.synthetic.main.fragment_enter_text.editor
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EnterTextFragment : Fragment() {
    protected val enterTextViewModel: EnterTextViewModel by sharedViewModel()
    private val eventViewModel: EventViewModel by sharedViewModel()

    private lateinit var enterTextBinding: FragmentEnterTextBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        enterTextBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_text, container, false)
        enterTextBinding.enterTextViewModel = enterTextViewModel
        enterTextBinding.lifecycleOwner = this
        return enterTextBinding.root
    }

    override fun onStart() {
        super.onStart()
        initEditor()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()
        editor.html = enterTextViewModel.text.value ?: ""
    }

    private fun initEditor() {
        editor.setEditorHeight(200)
        editor.setEditorFontSize(30)
        editor.setPadding(10, 10, 10, 10)
        editor.focusEditor()
        val inputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editor, InputMethodManager.SHOW_IMPLICIT)
        editor.setOnTextChangeListener {
            enterTextViewModel.textChanged(it)
        }
    }

    private fun setUpListeners() {
        enterTextViewModel.selectedTextColor.observe(this, Observer { newColor ->
            editor.setTextColor(newColor)
        })
        enterTextViewModel.boldClicked.observe(this, Observer {
            editor.setBold()
        })
        enterTextViewModel.italicClicked.observe(this, Observer {
            editor.setItalic()
        })
        enterTextViewModel.underlineClicked.observe(this, Observer {
            editor.setUnderline()
        })
        enterTextViewModel.selectedFontSize.observe(this, Observer { newSize ->
            editor.setFontSize(newSize.size)
        })
        eventViewModel.errorMessage.observe(this, Observer { errorMessageResourceId ->
                Toast.makeText(context, errorMessageResourceId, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "saving textdetail failed : ${getString(errorMessageResourceId)}")
        })
        eventViewModel.textSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_text_success, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        fun newInstance(): EnterTextFragment {
            return EnterTextFragment()
        }
    }
}