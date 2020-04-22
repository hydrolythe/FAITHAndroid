package be.hogent.faith.faith.cinema

import be.hogent.faith.databinding.FragmentCinemaCreateVideoBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import com.google.android.material.picker.CalendarConstraints
import com.google.android.material.picker.MaterialDatePicker
import com.google.android.material.picker.Month

class CinemaCreateVideoFragment : Fragment() {

    var navigation: CinemaCreateVideoFragmentNavigationListener? = null
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var binding: FragmentCinemaCreateVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cinema_create_video, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CinemaCreateVideoFragmentNavigationListener) {
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
        binding.rvVideoDetails.layoutManager = GridLayoutManager(activity, 6)
        binding.rvVideoDetails.adapter = detailThumbnailsAdapter
    }

    private fun startListeners() {

        binding.btnCinemaCancel.setOnClickListener {
        }
        binding.btnCinemaChooseDate.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker: MaterialDatePicker<*>
        builder
                .setTitleTextResId(R.string.daterange)
                .setCalendarConstraints(
                        CalendarConstraints.Builder()
                                .setEnd(Month.today())
                                .build()
                )
        picker = builder.build()
        picker.show(this.fragmentManager!!, picker.toString())
        picker.addOnPositiveButtonClickListener {
            // TODO
        }
    }

    companion object {
        fun newInstance(): CinemaCreateVideoFragment {
            return CinemaCreateVideoFragment()
        }
    }

    interface CinemaCreateVideoFragmentNavigationListener {
        fun startViewVideoFragment()
        fun goBack()
    }
}
