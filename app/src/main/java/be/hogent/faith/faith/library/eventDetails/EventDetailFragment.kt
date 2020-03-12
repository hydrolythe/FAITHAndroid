package be.hogent.faith.faith.library.eventDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventList.EventListViewModel
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatarName.
 */
private const val ARG_EVENTDETAIL_ID = "eventdetailid"

/**
 * A fragment representing a single Event detail screen, showing
 * all the different [Detail] objects for an event.
 */
class EventDetailFragment : Fragment() {

    /**
     * The Shared UserViewModel to get the details.
     */
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private val eventListViewModel: EventListViewModel by sharedViewModel {
        parametersOf(
            userViewModel.user.value
        )
    }

    private lateinit var eventId: UUID
    /**
     * ViewModel used to monitor the last selected event.
     */
    private val eventDetailsViewModel: EventDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            eventId = UUID.fromString(it.getString(ARG_EVENTDETAIL_ID))
        }

        val rootView = inflater.inflate(R.layout.fragment_library_eventdetails, container, false)
        eventDetailsViewModel.setEvent(eventListViewModel.getEvent(eventId)!!)
        setListeners()
        return rootView
    }

    /**
     * Sets up the observers. In this case it will observe the [eventDetailsViewModel] and observe
     * changes of its value. Whenever a new value (i.e. another event has been clicked) is observed
     * it deletes the pageradapter and set a new one with the desired [Detail] objects of the event.
     */
    private fun setListeners() {
    }

    companion object {
        /**
         * Creates a new instances of this fragment, with the index of the selected event.
         */
        fun newInstance(eventUuid: UUID): EventDetailFragment {
            return EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EVENTDETAIL_ID, eventUuid.toString())
                }
            }
        }
    }
}