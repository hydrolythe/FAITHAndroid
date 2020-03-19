package be.hogent.faith.faith.backpackScreen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.util.setErrorMessage
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveDetailDialog(private var detail: Detail) : DialogFragment() {
    private lateinit var saveDetailBinding: be.hogent.faith.databinding.DialogSaveBackpackdetailBinding

    private val backpackViewModel: BackpackViewModel by sharedViewModel()

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    companion object {
        fun newInstance(detail: Detail): SaveDetailDialog {
            return SaveDetailDialog(detail)
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

    private fun startListeners() {
        saveDetailBinding.btnSaveBackpack.setOnClickListener {
            val fileName = saveDetailBinding.txtSaveEventTitle.text.toString()
            val noEmptyString = checkEmptyString(fileName)
            val notMaxCharacters = checkMaxCharacters(fileName)
            if (noEmptyString && notMaxCharacters) {
                saveFile(fileName)
                dismiss()
            } else {
                if (!noEmptyString)
                    backpackViewModel.setErrorMessage(R.string.save_detail_emptyString)
                if (!notMaxCharacters)
                    backpackViewModel.setErrorMessage(R.string.save_detail_maxChar)
            }
        }

        saveDetailBinding.btnSaveBackpackCancel.setOnClickListener {
            dismiss()
            backpackViewModel.goToDetail(detail)
        }

        backpackViewModel.errorMessage.observe(this, Observer {
            saveDetailBinding.textInputLayoutDetailTitle.setErrorMessage(it)
        })
    }

    private fun checkMaxCharacters(fileName: String): Boolean {
        return fileName.length <= 30
    }

    private fun checkEmptyString(fileName: String): Boolean {
        return fileName.isNotEmpty() || !fileName.isBlank()
    }

    private fun saveFile(fileName: String) {
        detail.fileName = fileName
        backpackViewModel.saveCurrentDetail(userViewModel.user.value!!, detail)
    }
}