package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSaveEventBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveEventDialog : DialogFragment() {
    private lateinit var saveEventBinding: DialogSaveEventBinding

    private val eventDetailsViewModel: EventViewModel by sharedViewModel()

    companion object {
        fun newInstance(): SaveEventDialog {
            return SaveEventDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(activity, R.style.Dialog_NearlyFullScreen).apply {
            setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        saveEventBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_event, container, false)
        saveEventBinding.apply {
            eventViewModel = eventDetailsViewModel
            lifecycleOwner = this@SaveEventDialog
        }
        return saveEventBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    private fun startListeners() {
        eventDetailsViewModel.dateButtonClicked.observe(this, Observer {
            EventDateDialog.newInstance().show(fragmentManager!!, null)
        })
        eventDetailsViewModel.cancelButtonClicked.observe(this, Observer {
            dismiss()
        })
    }
}