package be.hogent.faith.faith.emotionCapture.enterText


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEnterTextBinding
import be.hogent.faith.util.TAG
import kotlinx.android.synthetic.main.fragment_enter_text.editor
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EnterTextFragment : Fragment() {
    protected val enterTextViewModel: EnterTextViewModel by sharedViewModel()

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

    private fun initEditor() {
        editor.setEditorHeight(200)
        editor.setEditorFontSize(30)
         //editor.setEditorBackgroundColor(Color.BLUE);
        //editor.setBackgroundColor(Color.BLUE);
        //editor.setBackgroundResource(R.drawable.bg);
        editor.setPadding(10, 10, 10, 10)
        //editor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        editor.setPlaceholder(".....")
        //editor.setInputEnabled(false);

        editor.setOnTextChangeListener {
            enterTextViewModel.textChanged(it);
        }

    }

    private fun setUpListeners() {
        enterTextViewModel.selectedTextColor.observe(this, Observer { newColor ->
            editor.setTextColor(newColor)
        })
        enterTextViewModel.boldClicked.observe(this, Observer { lineWidth ->
            editor.setBold()
        })
        enterTextViewModel.italicClicked.observe(this, Observer {
            editor.setItalic()
        })
        enterTextViewModel.underlineClicked.observe(this, Observer {
            editor.setUnderline()
        })
        enterTextViewModel.selectedFontSize.observe(this, Observer { newSize ->
            editor.setFontSize(newSize)
        })
    }

    companion object {
        fun newInstance(): EnterTextFragment {
            return EnterTextFragment()
        }
    }
}