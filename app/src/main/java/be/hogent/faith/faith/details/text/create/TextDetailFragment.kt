package be.hogent.faith.faith.details.text.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEnterTextBinding
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.backpack.BackpackScreenActivity
import be.hogent.faith.faith.cinema.CinemaActivity
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.treasureChest.TreasureChestActivity
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.fragment_enter_text.cardView_size
import kotlinx.android.synthetic.main.fragment_enter_text.enterText_editor
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import kotlin.reflect.KClass

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
        val existingDetail = requireArguments().getSerializable(TEXT_DETAIL) as TextDetail
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
        textDetailDetailViewModel.pickTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
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
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

        textDetailDetailViewModel.fontsizeClicked.observe(this, Observer {
            cardView_size.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        textDetailDetailViewModel.selectedFontSize.observe(this, Observer { newSize ->
            enterText_editor.setFontSize(newSize.size)
        })
        textDetailDetailViewModel.errorMessage.observe(this, Observer { errorMessageResourceId ->
            Toast.makeText(context, errorMessageResourceId, Toast.LENGTH_SHORT).show()
        })
        textDetailDetailViewModel.savedDetail.observe(this, Observer { savedTextDetail ->
            if (requireActivity() is EmotionCaptureMainActivity) {
                Toast.makeText(context, R.string.save_text_success, Toast.LENGTH_SHORT).show()
            }
            detailFinishedListener.onDetailFinished(savedTextDetail)
            navigation?.backToEvent()
        })
        textDetailDetailViewModel.customTextColorClicked.observe(this, Observer {
            val builder = ColorPickerDialog.Builder(this.context)
                .setTitle("Kies een kleur")
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)

                .setPositiveButton(
                    getString(R.string.confirm),
                    ColorEnvelopeListener { envelope, _ ->
                        if (envelope != null)
                            textDetailDetailViewModel.pickCustomTextColor(envelope.color)
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.show()
        })
        textDetailDetailViewModel.cancelClicked.observe(this, Observer {
            showExitAlert()
        })
        textDetailDetailViewModel.getDetailMetaData.observe(this, Observer {
            @Suppress("UNCHECKED_CAST") val saveDialog = DetailsFactory.createMetaDataDialog(
                requireActivity(),
                TextDetail::class as KClass<Detail>
            )
            if (saveDialog == null)
                textDetailDetailViewModel.setDetailsMetaData()
            else {
                saveDialog.setTargetFragment(this, 22)
                saveDialog.show(parentFragmentManager, null)
            }
        })
    }

    override fun onFinishSaveDetailsMetaData(title: String, dateTime: LocalDateTime) {
        textDetailDetailViewModel.setDetailsMetaData(title, dateTime)
    }

    private fun showExitAlert() {
        val alertDialog: AlertDialog = this.run {
            val builder = AlertDialog.Builder(this.requireContext()).apply {
                when (requireActivity()) {
                    is BackpackScreenActivity -> setTitle(R.string.dialog_to_the_backpack)
                    is CinemaActivity -> setTitle(R.string.dialog_to_the_cinema_title)
                    is TreasureChestActivity -> setTitle(R.string.dialog_to_the_treasurechest_title)
                    else -> setTitle(R.string.dialog_to_the_event_title)
                }
                setMessage(R.string.dialog_enterText_cancel_message)
                setPositiveButton(R.string.ok) { _, _ ->
                    navigation!!.backToEvent()
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    interface TextScreenNavigation {
        fun backToEvent()
    }
}