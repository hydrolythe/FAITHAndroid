package be.hogent.faith.faith.cinema

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TableLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCinemaStartBinding
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter

class CinemaStartScreenFragment : Fragment() {

    private var navigation: CinemaNavigationListener? = null
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var binding: FragmentCinemaStartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cinema_start, container, false)
        binding.lifecycleOwner = this

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
                emptyList(),
                requireNotNull(activity) as CinemaActivity
        )
        binding.rvCinema.layoutManager = GridLayoutManager(activity, 6)
        binding.rvCinema.adapter = detailThumbnailsAdapter
        binding.btnFilms.isChecked = true
    }

    private fun startListeners() {
        val toggle = CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->

            if (isChecked && compoundButton == binding.btnDetails) {
                binding.btnCinemaAdd.visibility = View.VISIBLE
                binding.btnDetails.backgroundTintList = ColorStateList.valueOf(Color.DKGRAY)
                binding.btnFilms.isChecked = false
                binding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            } else if (isChecked && compoundButton == binding.btnFilms) {
                binding.btnCinemaAdd.visibility = View.GONE
                binding.btnFilms.backgroundTintList = ColorStateList.valueOf(Color.DKGRAY)
                binding.btnDetails.isChecked = false
                binding.btnDetails.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            }
        }

        binding.btnDetails.setOnCheckedChangeListener(toggle)
        binding.btnFilms.setOnCheckedChangeListener(toggle)

        binding.btnCinemaAdd.setOnClickListener {
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
        }

        binding.btnCinemaCancel.setOnClickListener {
            navigation!!.closeCinema()
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
        fun closeCinema()
    }
}
