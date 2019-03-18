package be.hogent.faith.faith.takePhoto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSavePhotoBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SavePhotoDialogFragment : DialogFragment() {

    private lateinit var saveDialogBinding: DialogSavePhotoBinding

    private val takePhotoViewModel: TakePhotoViewModel by sharedViewModel()

    companion object {
        fun newInstance(): SavePhotoDialogFragment {
            return SavePhotoDialogFragment()
        }
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
        takePhotoViewModel.cancelButtonClicked.observe(this, Observer {
            dismiss()
        })
    }
}