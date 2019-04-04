package be.hogent.faith.faith.enterEventDetails

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import be.hogent.faith.databinding.DialogSaveEventBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class EventDateDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var saveEventBinding: DialogSaveEventBinding
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    companion object {
        fun newInstance(): EventDateDialog {
            return EventDateDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val now = LocalDateTime.now()
        return DatePickerDialog(context, this@EventDateDialog, now.year, now.monthValue, now.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        eventDetailsViewModel.eventDate.postValue(LocalDate.of(year, month, dayOfMonth).atStartOfDay())
    }
}
