package be.hogent.faith.faith.backpackScreen

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSaveBackpackdetailBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SaveDetailDialog(private var detail: Detail) : DialogFragment() {
    private lateinit var saveDetailBinding: DialogSaveBackpackdetailBinding

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
        saveDetailBinding.btnSaveDetail.setOnClickListener {
            backpackViewModel.onSaveClicked(saveDetailBinding.txtSaveBackpackDetailTitle.text.toString(), userViewModel.user.value!!, detail)
        }

        backpackViewModel.detailIsSaved.observe(this, Observer {
            Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT).show() // Mag deze algemeen blijven of best per detail ?
            dismiss()
        })

        saveDetailBinding.btnSaveBackpackCancel.setOnClickListener {
            dismiss()
            backpackViewModel.goToDetail(detail)
        }

        backpackViewModel.errorMessage.observe(this, Observer {
            if (it != null)
                Toast.makeText(context, resources.getString(it), Toast.LENGTH_SHORT).show()
        })

        // when a user starts typing error message will disappear
        saveDetailBinding.txtSaveBackpackDetailTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                userViewModel.clearErrorMessage()
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        backpackViewModel.clearErrorMessage()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        backpackViewModel.goToDetail(detail)
    }
}