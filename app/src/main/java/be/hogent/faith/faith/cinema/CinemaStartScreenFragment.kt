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
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class CinemaStartScreenFragment : Fragment() {

    private var navigation: CinemaNavigationListener? = null
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var binding: FragmentCinemaStartBinding
    private lateinit var addDetailMenu: PopupMenu

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel {
        parametersOf(
                userViewModel.user.value!!.cinema
        )
    }

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
        initialiseMenu()
    }

    private fun updateUI() {
        detailThumbnailsAdapter = DetailThumbnailsAdapter(
            emptyList(),
            requireNotNull(activity) as CinemaActivity
        )
        binding.rvCinema.layoutManager = GridLayoutManager(activity, 6)
        binding.rvCinema.adapter = detailThumbnailsAdapter
        binding.btnFilms.isChecked = true
    }

    private fun startListeners() {

        cinemaOverviewViewModel.makeFilmButtonClicked.observe(this, Observer {
            navigation?.startCreateVideoFragment()
        })
        val toggle =
            CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
                if (isChecked && compoundButton == binding.btnDetails) {
                    cinemaOverviewViewModel.onFilesButtonClicked()
                } else if (isChecked && compoundButton == binding.btnFilms) {
                    cinemaOverviewViewModel.onFilmsButtonClicked()
                }
            }
        binding.btnDetails.setOnCheckedChangeListener(toggle)
        binding.btnFilms.setOnCheckedChangeListener(toggle)

        cinemaOverviewViewModel.filesButtonClicked.observe(this, Observer {
            binding.btnCinemaAdd.visibility = View.VISIBLE
            binding.btnDetails.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
            binding.btnFilms.isChecked = false
            binding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)

            binding.textInputEditTextCinemaDetailsSearch.hint =
                resources.getString(R.string.search_hint_bestand)
        })

        cinemaOverviewViewModel.filmsButtonClicked.observe(this, Observer {
            binding.btnCinemaAdd.visibility = View.GONE
            binding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
            binding.btnDetails.isChecked = false
            binding.btnDetails.backgroundTintList =
                ColorStateList.valueOf(Color.TRANSPARENT)

            binding.textInputEditTextCinemaDetailsSearch.hint =
                resources.getString(R.string.search_hint_film)
        })

        cinemaOverviewViewModel.addButtonClicked.observe(this, Observer {
            addDetailMenu.show()
            /*
            val popupView = layoutInflater.inflate(R.layout.popup_window_cinema, null)

            val popupWindow = PopupWindow(
                popupView,
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            val help1 = -binding.btnCinemaAdd.height * 2

            popupWindow.showAsDropDown(binding.btnCinemaAdd, 40, help1 - popupWindow.height)
             */
        })

        cinemaOverviewViewModel.deleteEnabled.observe(this, Observer { enabled ->
            if (enabled) {
                detailThumbnailsAdapter!!.hide(false)
            } else {
                detailThumbnailsAdapter!!.hide(true)
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
            detailThumbnailsAdapter!!.updateDetailsList(list)
        })
    }

    @SuppressLint("RestrictedApi")
    private fun initialiseMenu() {
        addDetailMenu = PopupMenu(
            binding.btnCinemaAdd.context,
            binding.btnCinemaAdd,
            Gravity.TOP,
            0,
            R.style.PopupMenu_AddDetail
        )

        addDetailMenu.menuInflater.inflate(R.menu.menu_cinema, addDetailMenu.menu)

        addDetailMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.backpack_menu_addDrawing ->
                    navigation?.startDrawingDetailFragment()
                R.id.backpack_menu_addFile ->
                    navigation?.startExternalFileDetailFragment()
                R.id.backpack_menu_addFoto ->
                    navigation?.startPhotoDetailFragment()
            }
            true
        }
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(addDetailMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Timber.e("Error showing icons")
        }
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
        picker.show(this.requireFragmentManager(), picker.toString())
        picker.addOnPositiveButtonClickListener {
            // TODO
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
        fun startCreateVideoFragment()

        fun openDetailScreenFor(detail: Detail)

        fun closeCinema()
    }
}
