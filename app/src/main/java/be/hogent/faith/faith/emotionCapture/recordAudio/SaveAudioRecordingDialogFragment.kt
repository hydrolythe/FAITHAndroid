package be.hogent.faith.faith.emotionCapture.recordAudio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSaveAudioRecordingBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveAudioRecordingDialogFragment : DialogFragment() {

    private lateinit var saveDialogBinding: DialogSaveAudioRecordingBinding

    private val recordAudioViewModel: RecordAudioViewModel by sharedViewModel()

    companion object {
        fun newInstance(): SaveAudioRecordingDialogFragment {
            return SaveAudioRecordingDialogFragment()
        }
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
        recordAudioViewModel.cancelButtonClicked.observe(this, Observer {
            dismiss()
        })
    }
}