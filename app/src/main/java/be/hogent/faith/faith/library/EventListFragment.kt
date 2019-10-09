package be.hogent.faith.faith.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEventoverviewBinding
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventDetailsList.SelectedItemViewModel
import be.hogent.faith.faith.library.eventList.EventListener
import be.hogent.faith.faith.library.eventList.EventsAdapter
import com.bumptech.glide.Glide
import org.koin.android.ext.android.getKoin

class EventListFragment : Fragment() {

    private lateinit var binding: FragmentEventoverviewBinding

    /**
     * Adapter for the recyclerview showing the events i.e. [rv_EventList]
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
    private lateinit var selectedItemViewModel: SelectedItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_eventoverview, container, false)

        //TODO: change to Koin implementation
        selectedItemViewModel =
            ViewModelProviders.of(activity!!).get(SelectedItemViewModel::class.java)
        if (context!!.resources.getBoolean(R.bool.isTablet)) {
            displayMasterDetailLayout()
        } else {
            displaySingleLayout()
        }
        binding.eventlistLayout.rvEventlist
        setupRecyclerView(binding.eventlistLayout.rvEventlist)
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
                Log.i("Tag", "Selected $selectedIndex")
                selectedItemViewModel.selectedItem.value = selectedIndex
            }
        }
        eventsAdapter = EventsAdapter(eventListener, Glide.with(this))
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = eventsAdapter
        }
    }

    private fun startListeners() {
        userViewModel.user.observe(this, Observer<User> { user ->
            user?.let {
                eventsAdapter.apply {
                    events = user.events
                    notifyDataSetChanged()
                }
            }
        })
    }
}