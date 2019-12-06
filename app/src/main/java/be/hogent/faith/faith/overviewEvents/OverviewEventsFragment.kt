package be.hogent.faith.faith.overviewEvents

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.event_list.rv_eventlist
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.UUID

class OverviewEventsFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewEventsFragment()
    }

    private var navigation: OverviewEventsNavigationListener? = null

    private lateinit var eventsOverViewViewModel: OverviewEventsViewModel

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var eventsAdapter: EventsAdapter

    private lateinit var eventListener: EventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventsOverViewViewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_eventoverview, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OverviewEventsNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        setUpRecyclerView()
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

    private fun setUpRecyclerView() {
        eventListener = object : EventListener {
            override fun onEventClicked(eventUuid: UUID) {
                navigation?.startEventDetailsFragment(eventUuid)
            }
        }

        eventsAdapter = EventsAdapter(eventListener, Glide.with(this))
        rv_eventlist.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = eventsAdapter
        }
    }

    interface OverviewEventsNavigationListener {
        fun startEventDetailsFragment(eventUuid: UUID)
    }
}
