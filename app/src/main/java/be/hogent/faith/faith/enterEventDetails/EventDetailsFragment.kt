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
import be.hogent.faith.faith.UserViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.UUID

private const val ARG_EVENTUUID = "eventUUID"

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null
    private lateinit var userViewModel: UserViewModel
    //    private lateinit var eventDetailsViewModel: EventDetailsViewModel
    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()
    private lateinit var eventDetailsBinding: FragmentEnterEventDetailsBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    private lateinit var saveDialog: SaveEventDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = getViewModel()
        // Can't really do it like this because we need the sharedViewModel!
//        eventDetailsViewModel = if (arguments?.getSerializable(ARG_EVENTUUID) != null) {
//            getViewModel { parametersOf(userViewModel.user, arguments?.getSerializable(ARG_EVENTUUID)) }
//        } else {
//            getViewModel { parametersOf(userViewModel.user) }
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_event_details, container, false)
        eventDetailsBinding.eventViewModel = eventDetailsViewModel
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
            adapter = DetailThumbnailsAdapter(context, emptyList())
        }
        detailThumbnailsAdapter = eventDetailsBinding.recyclerViewEventDetailsDetails.adapter as DetailThumbnailsAdapter
    }

    private fun startListeners() {
        // Update event on user input
        eventDetailsViewModel.eventTitle.observe(this, Observer { newTitle ->
            eventDetailsViewModel.event.value?.let { event ->
                event.title = newTitle
            }
        })
        eventDetailsViewModel.eventDate.observe(this, Observer { newDate ->
            eventDetailsViewModel.event.value?.let { event ->
                event.dateTime = newDate
            }
        })

        // Listen to click events
        eventDetailsViewModel.emotionAvatarClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
        eventDetailsViewModel.cameraButtonClicked.observe(this, Observer {
            navigation?.startTakePhotoFragment()
        })
        eventDetailsViewModel.audioButtonClicked.observe(this, Observer {
            navigation?.startRecordAudioFragment()
        })
        eventDetailsViewModel.event.observe(this, Observer { event ->
            detailThumbnailsAdapter?.updateDetailsList(event.details)
        })
        eventDetailsViewModel.sendButtonClicked.observe(this, Observer {
            saveDialog = SaveEventDialog.newInstance()
            saveDialog.show(fragmentManager!!, null)
        })
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    interface EventDetailsNavigationListener {
        fun startDrawEmotionAvatarFragment()
        fun startTakePhotoFragment()
        fun startRecordAudioFragment()
    }

    companion object {
        fun newInstance(eventUuid: UUID? = null): EventDetailsFragment {
            return EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    eventUuid?.let {
                        putSerializable(ARG_EVENTUUID, it)
                    }
                }
            }
        }
    }
}