package be.hogent.faith.faith.cinema

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCinemaStartBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_cinema_start.btn_cinema_chooseDate
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CinemaStartScreenFragment : Fragment() {

    private var navigation: CinemaNavigationListener? = null
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var binding: FragmentCinemaStartBinding
    private lateinit var addDetailMenu: PopupMenu
    private var menuIsOpen: Boolean = false
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_cinema_start, container, false)
        binding.lifecycleOwner = this
        binding.cinemaOverviewViewModel = cinemaOverviewViewModel

        return binding.root
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
        updateUI()
    }

    private fun updateUI() {
        detailThumbnailsAdapter = DetailThumbnailsAdapter(
            requireNotNull(activity) as CinemaActivity
        )
        binding.rvCinema.layoutManager = GridLayoutManager(activity, 6)
        binding.rvCinema.adapter = detailThumbnailsAdapter
    }

    private fun startListeners() {

        cinemaOverviewViewModel.addButtonClicked.observe(this, Observer {
            menuIsOpen = menuIsOpen.not()
            if (menuIsOpen) {
                binding.popupWindowCinema.popupCinema.visibility = View.VISIBLE
                binding.btnCinemaAdd.setImageResource(R.drawable.ic_knop_sluit_opties)
            } else {
                binding.popupWindowCinema.popupCinema.visibility = View.GONE
                binding.btnCinemaAdd.setImageResource(R.drawable.add_btn)
            }
        })

        binding.popupWindowCinema.addDrawing.setOnClickListener {
            menuIsOpen = menuIsOpen.not()
            navigation?.startDrawingDetailFragment()
        }
        binding.popupWindowCinema.addExternalFile.setOnClickListener {
            menuIsOpen = menuIsOpen.not()
            navigation?.startExternalFileDetailFragment()
        }
        binding.popupWindowCinema.addPhoto.setOnClickListener {
            menuIsOpen = menuIsOpen.not()
            navigation?.startPhotoDetailFragment()
        }
        cinemaOverviewViewModel.makeFilmButtonClicked.observe(this, Observer {
            navigation?.startCreateFilmFragment()
        })
        val toggle =
            CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
                if (isChecked && compoundButton == binding.btnDetails) {
                    cinemaOverviewViewModel.onFilesButtonClicked()
                } else if (isChecked && compoundButton == binding.btnFilms) {
                    if (menuIsOpen) {
                        binding.popupWindowCinema.popupCinema.visibility = View.GONE
                        binding.btnCinemaAdd.setImageResource(R.drawable.add_btn)
                        menuIsOpen = false
                    }
                    cinemaOverviewViewModel.onFilmsButtonClicked()

                }
            }
        binding.btnDetails.setOnCheckedChangeListener(toggle)
        binding.btnFilms.setOnCheckedChangeListener(toggle)

        cinemaOverviewViewModel.filmsVisible.observe(this, Observer {
            if (!it) {
                binding.btnCinemaAdd.visibility = View.VISIBLE
                binding.btnDetails.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                binding.btnFilms.isChecked = false
                binding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)

                binding.textInputEditTextCinemaDetailsSearch.hint =
                    resources.getString(R.string.search_hint_bestand)
            } else {
                binding.btnCinemaAdd.visibility = View.GONE
                binding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                binding.btnDetails.isChecked = false
                binding.btnDetails.backgroundTintList =
                    ColorStateList.valueOf(Color.TRANSPARENT)

                binding.textInputEditTextCinemaDetailsSearch.hint =
                    resources.getString(R.string.search_hint_film)
            }
        })

        cinemaOverviewViewModel.filteredDetails.observe(this, Observer { details ->
            detailThumbnailsAdapter?.submitList(details)
        })

        cinemaOverviewViewModel.addButtonClicked.observe(this, Observer {
            addDetailMenu.show()
        })

        cinemaOverviewViewModel.deleteEnabled.observe(this, Observer { enabled ->
            if (enabled) {
                detailThumbnailsAdapter!!.setItemsAsDeletable(true)
            } else {
                detailThumbnailsAdapter!!.setItemsAsDeletable(false)
            }
        })

        cinemaOverviewViewModel.goToCityScreen.observe(this, Observer {
            navigation!!.closeCinema()
        })

        cinemaOverviewViewModel.dateRangeClicked.observe(this, Observer {
            showDateRangePicker()
        })

        cinemaOverviewViewModel.dateRangeString.observe(
            this, Observer { range -> btn_cinema_chooseDate.text = range })

        cinemaOverviewViewModel.filteredDetails.observe(this, Observer { list ->
            detailThumbnailsAdapter!!.submitList(list)
        })
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

    interface CinemaNavigationListener {
        fun startPhotoDetailFragment()
        fun startDrawingDetailFragment()
        fun startExternalFileDetailFragment()
        fun startCreateFilmFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeCinema()
    }
}
