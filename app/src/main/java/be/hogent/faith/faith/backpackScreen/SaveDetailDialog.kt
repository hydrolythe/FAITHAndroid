package be.hogent.faith.faith.backpackScreen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import kotlinx.android.synthetic.main.dialog_save_event.progress
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveDetailDialog : DialogFragment() {
    private lateinit var saveDetailBinding: be.hogent.faith.databinding.DialogSaveBackpackdetailBinding

    private val backpackViewModel: BackpackViewModel by sharedViewModel()

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    companion object {
        fun newInstance(): SaveDetailDialog {
            return SaveDetailDialog()
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
        saveDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_backpackdetail, container, false)
        saveDetailBinding.backpackViewModel = backpackViewModel
        saveDetailBinding.userViewModel = userViewModel
        saveDetailBinding.lifecycleOwner = this@SaveDetailDialog
        return saveDetailBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun startListeners() {
        saveDetailBinding.btnSaveBackpack.setOnClickListener {
            saveFile()
            dismiss()
        }

        saveDetailBinding.btnSaveBackpackCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveFile() {
        var detail = backpackViewModel.showSaveDialog.value
        detail!!.fileName = saveDetailBinding.txtSaveEventTitle.text.toString()
        backpackViewModel.saveCurrentDetail(detail)
    }
}