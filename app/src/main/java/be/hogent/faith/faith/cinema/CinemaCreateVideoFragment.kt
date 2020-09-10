package be.hogent.faith.faith.cinema

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCinemaCreateVideoBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailFragment
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.util.dismissDialog
import be.hogent.faith.faith.util.showLoadingDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_cinema_start.btn_cinema_chooseDate
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class CinemaCreateVideoFragment : Fragment(), DetailFragment<FilmDetail> {

    override lateinit var detailFinishedListener: DetailFinishedListener

    var navigation: CinemaCreateVideoFragmentNavigationListener? = null
    private lateinit var binding: FragmentCinemaCreateVideoBinding
    private val createVideoViewModel: CinemaCreateVideoViewModel by viewModel()
    private var selectedDetailsAdapter: SelectedDetailsAdapter? = null
    private val cinemaOverviewViewModel: CinemaOverviewViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_cinema_create_video,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = createVideoViewModel
        binding.cinemaOverviewViewModel = cinemaOverviewViewModel
        (binding.viewModel as CinemaCreateVideoViewModel).cinema = cinemaOverviewViewModel.detailsContainer

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CinemaCreateVideoFragmentNavigationListener) {
            navigation = context
        }
        if (context is DetailFinishedListener) {
            detailFinishedListener = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
    }

    private fun updateUI() {
        selectedDetailsAdapter = SelectedDetailsAdapter(object : SelectedDetailsClickListener {
            override fun selectDetail(detail: Detail, isSelected: Boolean): Boolean {
                val isAdded = createVideoViewModel.selectDetail(detail, getDetailDuration(detail))
                if (!isSelected && !isAdded)
                    Toast.makeText(
                        context,
                        resources.getString(R.string.selected_detail_error),
                        Toast.LENGTH_SHORT
                    ).show()
                return isAdded
            }
        })

        binding.rvVideoDetails.layoutManager = GridLayoutManager(activity, 6)
        binding.rvVideoDetails.adapter = selectedDetailsAdapter

        binding.progressBarCinema.max = VideoDurations.FILM_MAX_SECONDS
    }

    private fun startListeners() {
        binding.btnCinemaCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        cinemaOverviewViewModel.dateRangeClicked.observe(viewLifecycleOwner, Observer {
            showDateRangePicker()
        })

        cinemaOverviewViewModel.dateRangeString.observe(viewLifecycleOwner, Observer { range ->
            btn_cinema_chooseDate.text = range
        })

        createVideoViewModel.savedDetail.observe(viewLifecycleOwner, Observer { detail ->
            navigation?.onFilmFinished(detail)
        })

        binding.btnCreateVideo.setOnClickListener {
            createVideoViewModel.onSaveClicked()
        }

        createVideoViewModel.selectedDuration.observe(viewLifecycleOwner, Observer { duration ->
            binding.progressBarCinema.progress = duration
        })

        createVideoViewModel.encodingProgress.observe(viewLifecycleOwner, Observer { progress ->
            binding.progressBarCinema.max = 100
            binding.progressBarCinema.progress = progress
        })

        cinemaOverviewViewModel.filteredDetails.observe(viewLifecycleOwner, Observer {
            selectedDetailsAdapter!!.submitList(it)
        })
        createVideoViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessageId ->
            Toast.makeText(requireContext(), errorMessageId, Toast.LENGTH_SHORT).show()
        })

        createVideoViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if(isLoading){
                showLoadingDialog()
            }else{
                dismissDialog()
            }
        })

        createVideoViewModel.getDetailMetaData.observe(viewLifecycleOwner, Observer {
            @Suppress("UNCHECKED_CAST") val saveDialog = DetailsFactory.createMetaDataDialog(
                requireActivity(),
                FilmDetail::class as KClass<Detail>
            )
            if (saveDialog == null)
                createVideoViewModel.setDetailsMetaData()
            else {
                saveDialog.setTargetFragment(this, 22)
                saveDialog.show(parentFragmentManager, null)
            }
        })
    }

    private fun getDetailDuration(detail: Detail): Int {
        when (detail) {
            is DrawingDetail -> return VideoDurations.DRAWING_DURATION
            is PhotoDetail -> return VideoDurations.PHOTO_DURATION
            is VideoDetail -> return getVideoDuration(detail)
        }
        return 0
    }

    private fun getVideoDuration(videoDetail: VideoDetail): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.fromFile(videoDetail.file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(time).toLong()).toInt()
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
        fun newInstance(): CinemaCreateVideoFragment {
            return CinemaCreateVideoFragment()
        }
    }

    interface CinemaCreateVideoFragmentNavigationListener {
        fun onFilmFinished(detail: FilmDetail)
        fun goBack()
    }

    override fun onFinishSaveDetailsMetaData(title: String, dateTime: LocalDateTime) {
        createVideoViewModel.setDetailsMetaData(title, dateTime)
    }
}
