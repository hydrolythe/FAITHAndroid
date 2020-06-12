package be.hogent.faith.faith.cinema

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCinemaStartBinding
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.faith.detailscontainer.DetailsContainerFragment
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_cinema_start.btn_cinema_add
import kotlinx.android.synthetic.main.fragment_cinema_start.btn_cinema_chooseDate
import kotlinx.android.synthetic.main.fragment_cinema_start.btn_cinema_delete
import kotlinx.android.synthetic.main.fragment_cinema_start.rv_cinema
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CinemaStartScreenFragment : DetailsContainerFragment<Cinema>() {

    private var navigation: CinemaNavigationListener? = null
    override val addButton: ImageButton
        get() = btn_cinema_add
    override val deleteButton: ImageButton
        get() = btn_cinema_delete
    override val detailsRecyclerView: RecyclerView
        get() = rv_cinema

    override val layoutResourceID: Int = R.layout.fragment_cinema_start
    override val menuResourceID: Int = R.menu.menu_cinema

    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel()

    override val detailsContainerViewModel: DetailsContainerViewModel<Cinema>
        get() = cinemaOverviewViewModel

    private val cinemaBinding: FragmentCinemaStartBinding
        get() = binding as FragmentCinemaStartBinding

    override fun setViewModel() {
        cinemaBinding.cinemaOverviewViewModel = cinemaOverviewViewModel
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CinemaNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    private fun startListeners() {
        cinemaOverviewViewModel.makeFilmButtonClicked.observe(this, Observer {
            navigation?.startCreateFilmFragment()
        })
        val toggle =
            CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
                if (isChecked && compoundButton == cinemaBinding.btnDetails) {
                    cinemaOverviewViewModel.onFilesButtonClicked()
                } else if (isChecked && compoundButton == cinemaBinding.btnFilms) {
                    if (menuIsOpen) {
                        cinemaBinding.btnCinemaAdd.setImageResource(R.drawable.add_btn)
                        menuIsOpen = false
                    }
                    cinemaOverviewViewModel.onFilmsButtonClicked()
                }
            }
        cinemaBinding.btnDetails.setOnCheckedChangeListener(toggle)
        cinemaBinding.btnFilms.setOnCheckedChangeListener(toggle)

        cinemaOverviewViewModel.filmsVisible.observe(this, Observer { filmsVisible ->
            if (!filmsVisible) {
                cinemaBinding.btnCinemaAdd.visibility = View.VISIBLE
                cinemaBinding.btnDetails.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                cinemaBinding.btnFilms.isChecked = false
                cinemaBinding.btnFilms.backgroundTintList =
                    ColorStateList.valueOf(Color.TRANSPARENT)

                cinemaBinding.textInputEditTextCinemaDetailsSearch.hint =
                    resources.getString(R.string.search_hint_bestand)
            } else {
                cinemaBinding.btnCinemaAdd.visibility = View.GONE
                cinemaBinding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                cinemaBinding.btnDetails.isChecked = false
                cinemaBinding.btnDetails.backgroundTintList =
                    ColorStateList.valueOf(Color.TRANSPARENT)

                cinemaBinding.textInputEditTextCinemaDetailsSearch.hint =
                    resources.getString(R.string.search_hint_film)
            }
        })

        cinemaOverviewViewModel.goToCityScreen.observe(this, Observer {
            navigation!!.closeScreen()
        })

        cinemaOverviewViewModel.dateRangeClicked.observe(this, Observer {
            showDateRangePicker()
        })

        cinemaOverviewViewModel.dateRangeString.observe(
            this, Observer { range -> btn_cinema_chooseDate.text = range })
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker: MaterialDatePicker<*>
        builder
            .setTitleText(R.string.daterange)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
                    .build()
            )
        picker = builder.build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            cinemaOverviewViewModel.setDateRange(it.first, it.second)
        }
        picker.addOnNegativeButtonClickListener {
            cinemaOverviewViewModel.resetDateRange()
        }
    }

    companion object {
        fun newInstance(): CinemaStartScreenFragment {
            return CinemaStartScreenFragment()
        }
    }

    interface CinemaNavigationListener : DetailsContainerNavigationListener {
        fun startCreateFilmFragment()
    }
}
