package be.hogent.faith.faith.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.library.eventList.EventListener
import be.hogent.faith.faith.library.eventList.EventsAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.event_list.event_detail_container
import kotlinx.android.synthetic.main.event_list.rv_eventlist
import org.koin.android.ext.android.getKoin
import java.util.UUID

/**
 * An activity representing a list of Events of the user. This activity
 * has different presentations for small and bigger devices. On
 * smaller devices, the activity presents a list of events, which when touched,
 * lead to a [EventDetailActivity] representing
 * event details. On nbigger devices, the activity presents the list of events and
 * events details side-by-side using two vertical panes.
 */
class EventListActivity : AppCompatActivity() {


    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private lateinit var eventsAdapter: EventsAdapter

    private lateinit var eventListener: EventListener

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        if (event_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        setupRecyclerView(rv_eventlist)
    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView(rv_eventlist)
        startListeners()
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
