package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.emotionCapture.editDetail.DetailType
import be.hogent.faith.util.TAG
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.UUID

private const val ARG_EVENTUUID = "eventUUID"

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null

    private lateinit var userViewModel: UserViewModel
    private val eventViewModel: EventViewModel by sharedViewModel()

    private lateinit var eventDetailsBinding: be.hogent.faith.databinding.FragmentEnterEventDetailsBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

    private lateinit var saveDialog: SaveEventDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = getViewModel()

        // When an UUID is given the [eventViewModel] should be updated to show the given event's state.
        arguments?.getSerializable(ARG_EVENTUUID)?.let {
            eventViewModel.setEvent(it as UUID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_enter_event_details, container, false)
        eventDetailsBinding.eventViewModel = eventViewModel
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
        detailThumbnailsAdapter =
            eventDetailsBinding.recyclerViewEventDetailsDetails.adapter as DetailThumbnailsAdapter
    }

    private fun startListeners() {
        eventViewModel.inputErrorMessageID.observe(this, Observer { errorMessageID ->
            Toast.makeText(context, errorMessageID, Toast.LENGTH_LONG).show()
        })

        // Update adapter when event changes
        eventViewModel.event.observe(this, Observer { event ->
            detailThumbnailsAdapter?.updateDetailsList(event.details)
        })

        // Listen to click events
        eventViewModel.emotionAvatarClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
        eventViewModel.cameraButtonClicked.observe(this, Observer {
            // navigation?.startTakePhotoFragment()
            navigation?.startEventDetail(DetailType.PICTURE)
        })
        eventViewModel.audioButtonClicked.observe(this, Observer {
            // navigation?.startRecordAudioFragment()
            navigation?.startEventDetail(DetailType.AUDIO)
        })
        eventViewModel.sendButtonClicked.observe(this, Observer {
            saveDialog = SaveEventDialog.newInstance()
            saveDialog.show(fragmentManager!!, null)
        })
        eventViewModel.eventSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.toast_save_event_success, Toast.LENGTH_LONG).show()
            saveDialog.dismiss()

            // Go back to main screen
            fragmentManager!!.popBackStack()
        })
        eventViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Log.e(TAG, errorMessage)
            // TODO: let others provide resource strings, not strings when showing error messages
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            saveDialog.dismiss()
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