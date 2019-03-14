package be.hogent.faith.faith.recordAudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSaveAudioRecordingBinding
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

const val ARG_TEMP_RECORDING_FILE = "arg_temp_recording_file"

class SaveAudioRecordingDialogFragment : DialogFragment() {

    private lateinit var saveDialogBinding: DialogSaveAudioRecordingBinding

    private lateinit var tempRecordingFile: File


    private val recordAudioViewModel: RecordAudioViewModel by viewModel {
        parametersOf(
            //TODO: change to use current event. Use UUID or Event itself?
            tempRecordingFile, Event()
        )
    }
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()


    companion object {
        fun newInstance(tempRecordingFile: File): SaveAudioRecordingDialogFragment {
            return SaveAudioRecordingDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TEMP_RECORDING_FILE, tempRecordingFile)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempRecordingFile = arguments!!.getSerializable(ARG_TEMP_RECORDING_FILE) as File
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        saveDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_audio_recording, container, false)
        saveDialogBinding.apply {
            recordAudioViewModel = this@SaveAudioRecordingDialogFragment.recordAudioViewModel
            lifecycleOwner = this@SaveAudioRecordingDialogFragment

        }
        return saveDialogBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    private fun startListeners() {
        recordAudioViewModel.recordingSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, getString(R.string.toast_saved_successfully), Toast.LENGTH_SHORT).show()
            dismiss()
        })
        recordAudioViewModel.recordingSaveFailed.observe(this, Observer {
            Toast.makeText(context, getString(R.string.toast_save_failed), Toast.LENGTH_SHORT).show()
            dismiss()
        })
        recordAudioViewModel.cancelButtonClicked.observe(this, Observer {
            dismiss()
        })
    }
}