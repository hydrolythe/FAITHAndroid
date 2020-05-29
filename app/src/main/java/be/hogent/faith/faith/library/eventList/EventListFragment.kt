package be.hogent.faith.faith.library.eventList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentLibraryEventlistBinding
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_library_eventlist.*
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

            override fun onEventDeleteClicked(event: Event) {
                showDeleteAlert(event)
            }
        }
        eventsAdapter = EventsAdapter(eventListener)
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridSpacingItemDecoration(3, 10, true, 0))
            this.adapter = eventsAdapter
        }
    }

    private fun startListeners() {

        eventListViewModel.dateRangeString.observe(viewLifecycleOwner, Observer { range ->
            btn_library_eventlist_chooseDate.text = range
        })

        eventListViewModel.filteredEvents.observe(viewLifecycleOwner, Observer { list ->
            eventsAdapter.updateEventsList(list, eventListViewModel.deleteEnabled.value!!)
        })

        eventListViewModel.audioFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchAudio,
                R.drawable.ic_filterknop_audio,
                R.drawable.ic_filterknop_audio_selected
            )
        })
        eventListViewModel.textFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchText,
                R.drawable.ic_filterknop_teksten,
                R.drawable.ic_filterknop_teksten_selected
            )
        })
        eventListViewModel.photoFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchPhotos,
                R.drawable.ic_filterknop_foto,
                R.drawable.ic_filterknop_foto_selected
            )
        })
        eventListViewModel.drawingFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setDrawable(
                enabled,
                btn_library_eventlist_searchDrawing,
                R.drawable.ic_filterknop_tekeningen,
                R.drawable.ic_filterknop_tekeningen_selected
            )
        })

        eventListViewModel.startDateClicked.observe(viewLifecycleOwner, Observer {
            showDateRangePicker()
        })

        eventListViewModel.endDateClicked.observe(viewLifecycleOwner, Observer {
            showDateRangePicker()
        })

        eventListViewModel.cancelButtonClicked.observe(viewLifecycleOwner, Observer {
            requireActivity().onBackPressed()
        })

        eventListViewModel.deleteEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            eventsAdapter.updateEventsList(eventListViewModel.filteredEvents.value!!, enabled)
        })
    }

    /**
     * show Material DatePicker. Sets the latest month that can be selected
     */
    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker: MaterialDatePicker<*>
        builder
            .setTitleText(R.string.datumVan)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
                    .build()
            )
        picker = builder.build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            eventListViewModel.setDateRange(it.first, it.second)
        }
    }

    private fun showDeleteAlert(event: Event) {
        val alertDialog: AlertDialog = this.run {
            val builder = AlertDialog.Builder(this.requireContext()).apply {
                setTitle(getString(R.string.library_verwijder_event_title))
                setMessage(getString(R.string.library_verwijder_event_message))
                setPositiveButton(R.string.ok) { _, _ ->
                    eventListViewModel.deleteEvent(event)
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun setDrawable(enabled: Boolean, button: ImageButton, image: Int, imageSelected: Int) {
        button.setImageDrawable(
            AppCompatResources.getDrawable(
                this.requireContext(),
                if (enabled) imageSelected else image
            )
        )
    }

    interface EventsListNavigationListener {
        fun startEventDetailsFragment(eventUuid: UUID)
    }

    companion object {
        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }
}