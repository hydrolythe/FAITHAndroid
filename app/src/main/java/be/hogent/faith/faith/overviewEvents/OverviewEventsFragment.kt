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
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.UserViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragments_overview_events.rv_events
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

class OverviewEventsFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewEventsFragment()
    }

    private var navigation: OverviewEventsNavigationListener? = null

    private lateinit var eventsOverViewViewModel: OverviewEventsViewModel

    private lateinit var eventsAdapter: EventsAdapter

    private lateinit var eventListener: EventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userSessionScope = getKoin().getScope(KoinModules.USER_SCOPE_ID)
        get<UserViewModel>(scope = userSessionScope).let {
            eventsOverViewViewModel = getViewModel { parametersOf(it.user) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragments_overview_events, container, false)
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
        eventsOverViewViewModel.user.observe(this, Observer<User> { user ->
            user?.let {
                eventsAdapter.apply {
                    events = user.events
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
        rv_events.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = eventsAdapter
        }
    }

    interface OverviewEventsNavigationListener {
        fun startEventDetailsFragment(eventUuid: UUID)
    }
}
