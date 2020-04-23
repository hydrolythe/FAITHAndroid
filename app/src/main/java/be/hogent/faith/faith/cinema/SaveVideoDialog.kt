package be.hogent.faith.faith.cinema

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogCinemaSaveVideoBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin

class SaveVideoDialog : DialogFragment() {
    private lateinit var saveVideoBinding: DialogCinemaSaveVideoBinding

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    companion object {
        fun newInstance(): SaveVideoDialog {
            return SaveVideoDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireActivity(), R.style.Dialog_NearlyFullScreen).apply {
            setStyle(STYLE_NO_TITLE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveVideoBinding =
                DataBindingUtil.inflate(inflater, R.layout.dialog_cinema_save_video, container, false)
        saveVideoBinding.userViewModel = userViewModel
        saveVideoBinding.lifecycleOwner = this@SaveVideoDialog
        return saveVideoBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    private fun startListeners() {
        // TODO
    }
}