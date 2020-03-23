package be.hogent.faith.faith.backpackScreen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail

class DeleteDetailDialog(private var detail: Detail) : DialogFragment(){

    internal lateinit var listener: DeleteDetailDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.confirmation_delete_detail)
                .setPositiveButton(R.string.ok
                ) { dialog, id ->
                    listener.onDetailDeleteClick(this, detail)
                }
                .setNegativeButton(R.string.cancel
                ) { dialog, id ->
                    listener.onDetailCancelClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DeleteDetailDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement DeleteDetailDialogListener"))
        }
    }

    companion object {
        fun newInstance(detail: Detail): DeleteDetailDialog {
            return DeleteDetailDialog(detail)
        }
    }

    interface DeleteDetailDialogListener {
        fun onDetailDeleteClick(dialog: DialogFragment, detail: Detail)
        fun onDetailCancelClick(dialog: DialogFragment)
    }

}