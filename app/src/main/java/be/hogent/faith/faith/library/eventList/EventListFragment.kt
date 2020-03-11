package be.hogent.faith.faith.library.eventList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentLibraryEventlistBinding
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventDetails.EventDetailsViewModel
import com.bumptech.glide.Glide
import org.koin.android.ext.android.getKoin
import timber.log.Timber

class EventListFragment : Fragment() {

    private lateinit var binding: FragmentLibraryEventlistBinding

    /**
     * Adapter for the recyclerview showing the events
     */
    private lateinit var eventsAdapter: EventsAdapter

    /**
     * Listener used to track items clicked in the Adapter.
     */
    private lateinit var eventListener: EventListener

    /**
     * The userViewModel required to get all the information on events and details.
     */
    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    /**
     * The Viewmodel used to track the selected item.
     */
    private lateinit var eventDetailsViewModel: EventDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_library_eventlist, container, false)
        setupRecyclerView(binding.recyclerViewLibraryEventlist)
        startListeners()
        return binding.root
    }

    private fun displayMasterDetailLayout() {
    }

    private fun displaySingleLayout() {
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        eventListener = object : EventListener {
            override fun onEventClicked(selectedIndex: Int) {
                Timber.i("Selected $selectedIndex")
                eventDetailsViewModel.selectedItem.value = selectedIndex
            }
        }
        eventsAdapter = EventsAdapter(eventListener, Glide.with(this))
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            this.adapter = eventsAdapter
        }
    }

    private fun startListeners() {
        userViewModel.user.observe(this, Observer<User> { user ->
            user?.let {
                eventsAdapter.apply {
                    events = user.events.sortedByDescending { it.dateTime }
                    notifyDataSetChanged()
                }
            }
        })
    }
}