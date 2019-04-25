package be.hogent.faith.faith.emotionCapture.enterEventDetails

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
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.emotionCapture.editDetail.DetailType
import kotlinx.android.synthetic.main.fragment_enter_event_details.editText_event_details_event_title
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

private const val ARG_EVENTUUID = "eventUUID"

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null
    private lateinit var userViewModel: UserViewModel
    private lateinit var eventDetailsViewModel: EventDetailsViewModel
    private lateinit var eventDetailsBinding: FragmentEnterEventDetailsBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = getViewModel()
        if (arguments?.getSerializable(ARG_EVENTUUID) != null) {
            eventDetailsViewModel =
                getViewModel { parametersOf(userViewModel.user.value?: User(), arguments?.getSerializable(ARG_EVENTUUID)) }
        } else {
            eventDetailsViewModel =
                getViewModel { parametersOf(userViewModel.user.value?:User()) }
        }
    }

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
            adapter = DetailThumbnailsAdapter(context, emptyList())
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
            // navigation?.startTakePhotoFragment()
            navigation?.startEventDetail(DetailType.PICTURE)
        })
        eventDetailsViewModel.audioButtonClicked.observe(this, Observer {
            // navigation?.startRecordAudioFragment()
            navigation?.startEventDetail(DetailType.AUDIO)
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
        fun startRecordAudioFragment()
        fun startEventDetail(type: DetailType)
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