package be.hogent.faith.faith.enterEventDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEnterEventDetailsBinding
import be.hogent.faith.domain.models.Detail
import org.koin.android.viewmodel.ext.android.sharedViewModel

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()
    private lateinit var eventDetailsBinding: FragmentEnterEventDetailsBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_event_details, container, false)
        eventDetailsBinding.eventDetailsVM = eventDetailsViewModel
        eventDetailsBinding.lifecycleOwner = this
        return eventDetailsBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventDetailsNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
    }

    private fun updateUI() {
        eventDetailsBinding.recyclerViewEventDetailsDetails.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // Start with empty list and then fill it in
            adapter = DetailThumbnailsAdapter(context, emptyList<Detail>())
        }
        detailThumbnailsAdapter = eventDetailsBinding.recyclerViewEventDetailsDetails.adapter as DetailThumbnailsAdapter
    }

    private fun startListeners() {
        eventDetailsViewModel.eventTitle.observe(this, Observer { newTitle ->
            eventDetailsViewModel.event.value?.let { event ->
                event.title = newTitle
            }
        })
        eventDetailsViewModel.emotionAvatarClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
        eventDetailsViewModel.cameraButtonClicked.observe(this, Observer {
            navigation?.startTakePhotoFragment()
        })
        eventDetailsViewModel.event.observe(this, Observer { event ->
            detailThumbnailsAdapter?.updateDetailsList(event.details)
        })
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    interface EventDetailsNavigationListener {
        fun startDrawEmotionAvatarFragment()
        fun startTakePhotoFragment()
    }

    companion object {
        fun newInstance(): EventDetailsFragment {
            return EventDetailsFragment()
        }
    }
}