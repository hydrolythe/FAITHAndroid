package be.hogent.faith.faith.library.eventDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin

/**
 * A fragment representing a single Event detail screen, showing
 * all the different [Detail] objects for an event.
 */
class EventDetailFragment : Fragment() {

    /**
     * The viewpager showing fragments for the different details.
     */
    private lateinit var mPager: ViewPager2

    /**
     * The Shared UserViewModel to get the details.
     * TODO : this should be refactored by creating a new ViewModel managing the details via a repository
     */
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    /**
     * ViewModel used to monitor the last selected [Event].
     */
    private lateinit var eventDetailsViewModel: EventDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_eventdetails, container, false)
        eventDetailsViewModel =
            ViewModelProviders.of(activity!!).get(EventDetailsViewModel::class.java)
        setListeners()
        return rootView
    }

    /**
     * Sets up the observers. In this case it will observe the [eventDetailsViewModel] and observe
     * changes of its value. Whenever a new value (i.e. another event has been clicked) is observed
     * it deletes the pageradapter and set a new one with the desired [Detail] objects of the [Event].
     */
    private fun setListeners() {
    }
}
