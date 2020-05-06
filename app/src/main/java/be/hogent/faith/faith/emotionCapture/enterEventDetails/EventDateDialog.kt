package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class EventDateDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val eventDetailsViewModel: EventViewModel by sharedViewModel()

    companion object {
        fun newInstance(): EventDateDialog {
            return EventDateDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val now = LocalDateTime.now()
        return DatePickerDialog(requireContext(), this@EventDateDialog, now.year, now.monthValue-1, now.dayOfMonth)
    }

    // Month index starts at 0
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        eventDetailsViewModel.eventDate.postValue(LocalDate.of(year, month + 1, dayOfMonth).atStartOfDay())
    }
}
