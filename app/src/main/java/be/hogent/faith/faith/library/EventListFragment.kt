package be.hogent.faith.faith.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentEventoverviewBinding
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventList.EventListener
import be.hogent.faith.faith.library.eventList.EventsAdapter
import com.bumptech.glide.Glide
import org.koin.android.ext.android.getKoin
import java.util.UUID

class EventListFragment : Fragment() {

    private lateinit var binding: FragmentEventoverviewBinding

    private lateinit var eventsAdapter: EventsAdapter

    private lateinit var eventListener: EventListener

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_eventoverview, container, false)

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
            override fun onEventClicked(eventUuid: UUID) {
                Log.i("Tag", "Clicked")
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