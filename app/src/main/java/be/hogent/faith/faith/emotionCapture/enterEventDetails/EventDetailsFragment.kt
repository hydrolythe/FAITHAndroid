package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.content.Context
import android.os.Bundle
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
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.editDetail.DetailType
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.UUID

private const val ARG_EVENTUUID = "eventUUID"

class EventDetailsFragment : Fragment() {

    private var navigation: EventDetailsNavigationListener? = null

    private val eventViewModel: EventViewModel by sharedViewModel()
    private val userViewModel: UserViewModel = get(scope = getKoin().getScope(KoinModules.USER_SCOPE_ID))

    private lateinit var eventDetailsBinding: be.hogent.faith.databinding.FragmentEnterEventDetailsBinding

    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null

/***
 * Momenteel in commentaar gezet omdat de save Event knop nu op de details wordt getoond. In de toekomst zal het misschien toch nodig
 * zijn als beslist wordt om een event op te slaan als bvb enkel de avatar al is ingekleurd
   private lateinit var saveDialog: SaveEventDialog
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // When an UUID is given the [eventViewModel] should be updated to show the given event's state.
        arguments?.getSerializable(ARG_EVENTUUID)?.let {
            eventViewModel.setEvent(it as UUID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        eventDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_enter_event_details, container, false)
        eventDetailsBinding.eventViewModel = eventViewModel
        eventDetailsBinding.lifecycleOwner = this@EventDetailsFragment

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
        // Update adapter when event changes
        eventViewModel.event.observe(this, Observer { event ->
            detailThumbnailsAdapter?.updateDetailsList(event.details)
        })

        // Four main actions
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
        eventViewModel.drawingButtonClicked.observe(this, Observer {
            navigation?.startMakeDrawingFragment()
        })

        eventViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        })

        /***
         * Momenteel in commentaar gezet omdat de save Event knop nu op de details wordt getoond. In de toekomst zal het misschien toch nodig
         * zijn als beslist wordt om een event op te slaan als bvb enkel de avatar al is ingekleurd
        eventViewModel.sendButtonClicked.observe(this, Observer {
            saveDialog = SaveEventDialog.newInstance()
            saveDialog.show(fragmentManager!!, null)
        })

        userViewModel.eventSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_event_success, Toast.LENGTH_LONG).show()
            saveDialog.dismiss()

            // Go back to main screen
            fragmentManager!!.popBackStack()
        })

        userViewModel.eventSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_event_success, Toast.LENGTH_LONG).show()
            // Go back to main screen TOOD: not working right now
            fragmentManager!!.popBackStack()
        })
        userViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        })
        */
    }

    override fun onStop() {
        super.onStop()
        detailThumbnailsAdapter = null
    }

    interface EventDetailsNavigationListener {
        fun startDrawEmotionAvatarFragment()
        fun startTakePhotoFragment()
        fun startRecordAudioFragment()
        fun startMakeDrawingFragment()

        // TODO: verwijderen van DetailType
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