package be.hogent.faith.faith.cinema

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class CinemaDateDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel()

    companion object {
        fun newInstance(): CinemaDateDialog {
            return CinemaDateDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val now = LocalDateTime.now()
        return DatePickerDialog(requireContext(), this@CinemaDateDialog, now.year, now.monthValue, now.dayOfMonth)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        cinemaOverviewViewModel.detailDate.postValue(LocalDate.of(year, month, dayOfMonth).atStartOfDay())
    }
}
