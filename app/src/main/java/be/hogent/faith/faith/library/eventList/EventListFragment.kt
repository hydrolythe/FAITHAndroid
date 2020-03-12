package be.hogent.faith.faith.library.eventList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentLibraryEventlistBinding
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_library_eventlist.btn_library_eventlist_searchAudio
import kotlinx.android.synthetic.main.fragment_library_eventlist.btn_library_eventlist_searchPhotos
import kotlinx.android.synthetic.main.fragment_library_eventlist.btn_library_eventlist_searchDrawing
import kotlinx.android.synthetic.main.fragment_library_eventlist.btn_library_eventlist_searchText
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

class EventListFragment : Fragment() {

    private var navigation: EventsListNavigationListener? = null

    private lateinit var binding: FragmentLibraryEventlistBinding
    /**
     * Adapter for the recyclerview showing the events
     */
    private lateinit var eventsAdapter: EventsAdapter

    /**
     * Listener used to track items clicked in the Adapter.
     */
    private lateinit var eventListener: EventListener

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    private val eventListViewModel: EventListViewModel by sharedViewModel {
        parametersOf(
            userViewModel.user.value
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_library_eventlist, container, false)
        binding.eventlistViewModel = eventListViewModel
        setupRecyclerView(binding.recyclerViewLibraryEventlist)
        startListeners()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventsListNavigationListener) {
            navigation = context
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        eventListener = object : EventListener {
            override fun onEventClicked(eventUUID: UUID) {
                navigation?.startEventDetailsFragment(eventUUID)
            }
        }
        eventsAdapter = EventsAdapter(eventListener, Glide.with(this))
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridSpacingItemDecoration(3, 10, true, 0))
            this.adapter = eventsAdapter
        }
    }

    private fun startListeners() {
        eventListViewModel.filteredEvents.observe(this, Observer { list ->
            eventsAdapter.apply {
                events = list
                notifyDataSetChanged()
            }
        })
        eventListViewModel.audioFilterEnabled.observe(this, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchAudio,
                R.drawable.ic_filterknop_audio,
                R.drawable.ic_filterknop_audio_selected
            )
        })
        eventListViewModel.textFilterEnabled.observe(this, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchText,
                R.drawable.ic_filterknop_teksten,
                R.drawable.ic_filterknop_teksten_selected
            )
        })
        eventListViewModel.photoFilterEnabled.observe(this, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchPhotos,
                R.drawable.ic_filterknop_foto,
                R.drawable.ic_filterknop_foto_selected
            )
        })
        eventListViewModel.drawingFilterEnabled.observe(this, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchDrawing,
                R.drawable.ic_filterknop_tekeningen,
                R.drawable.ic_filterknop_tekeningen_selected
            )
        })
    }

    private fun setDrawable(enabled: Boolean, button: ImageButton, image: Int, imageSelected: Int) {
        button.setImageDrawable(
            AppCompatResources.getDrawable(
                this.context!!,
                if (enabled) imageSelected else image
            )
        )
    }

    interface EventsListNavigationListener {
        fun startEventDetailsFragment(eventUuid: UUID)
    }
}