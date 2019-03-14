package be.hogent.faith.faith.takePhoto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSavePhotoBinding
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.TAG
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

const val ARG_TEMP_PHOTO_FILE = "arg_temp_recording_file"

class SavePhotoDialogFragment : DialogFragment() {

    private lateinit var tempRecordingFile: File
    private lateinit var saveDialogBinding: DialogSavePhotoBinding

    private val takePhotoViewModel: TakePhotoViewModel by viewModel {
        parametersOf(
            // TODO: change to use current event. Use UUID or Event itself?
            tempRecordingFile, Event()
        )
    }

    companion object {
        fun newInstance(tempRecordingFile: File): SavePhotoDialogFragment {
            return SavePhotoDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TEMP_PHOTO_FILE, tempRecordingFile)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tempRecordingFile = arguments!!.getSerializable(ARG_TEMP_PHOTO_FILE) as File
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        saveDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_photo, container, false)
        saveDialogBinding.apply {
            takePhotoViewModel = this@SavePhotoDialogFragment.takePhotoViewModel
            lifecycleOwner = this@SavePhotoDialogFragment
        }
        return saveDialogBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    private fun startListeners() {
        takePhotoViewModel.photoSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, getString(R.string.toast_foto_saved_success), Toast.LENGTH_SHORT).show()
            dismiss()
        })
        takePhotoViewModel.recordingSaveFailed.observe(this, Observer {
            Log.e(TAG, it)
            Toast.makeText(context, getString(R.string.toast_save_photo_failed), Toast.LENGTH_SHORT).show()
            dismiss()
        })
        takePhotoViewModel.cancelButtonClicked.observe(this, Observer {
            dismiss()
        })
    }
}