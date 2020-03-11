package be.hogent.faith.faith.library.eventList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.library.eventfilters.CombinedEventFilter
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import org.threeten.bp.LocalDate

class EventListViewModel(private val getEventsUseCase: GetEventsUseCase) : ViewModel() {
    // Currently like this to do testing
    lateinit var events: List<Event>

    private val eventFilter = CombinedEventFilter()

    val searchString = MutableLiveData<String>()

    val startDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.MIN.plusDays(1)
    }
    val endDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.MAX.minusDays(1)
    }

    val audioFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val drawingFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val photoFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val textFilterEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    val filteredEvents: LiveData<List<Event>> = MediatorLiveData<List<Event>>().apply {
        addSource(searchString) { query ->
            eventFilter.titleFilter.searchString = query
            value = eventFilter.filter(events)
        }
        addSource(startDate) { startDate ->
            eventFilter.dateFilter.startDate = startDate
            value = eventFilter.filter(events)
        }
        addSource(endDate) { endDate ->
            eventFilter.dateFilter.endDate = endDate
            value = eventFilter.filter(events)
        }
        addSource(drawingFilterEnabled) { enabled ->
            eventFilter.hasDrawingDetailFilter.isEnabled = enabled
            value = eventFilter.filter(events)
        }
        addSource(textFilterEnabled) { enabled ->
            eventFilter.hasTextDetailFilter.isEnabled = enabled
            value = eventFilter.filter(events)
        }
        addSource(photoFilterEnabled) { enabled ->
            eventFilter.hasPhotoDetailFilter.isEnabled = enabled
            value = eventFilter.filter(events)
        }
        addSource(audioFilterEnabled) { enabled ->
            eventFilter.hasAudioDetailFilter.isEnabled = enabled
            value = eventFilter.filter(events)
        }
    }

    fun onFilterPhotosClicked() {
        photoFilterEnabled.value = photoFilterEnabled.value!!.not()
    }

    fun onFilterTextClicked() {
        textFilterEnabled.value = textFilterEnabled.value!!.not()
    }

    fun onFilterDrawingClicked() {
        drawingFilterEnabled.value = drawingFilterEnabled.value!!.not()
    }

    fun onFilterAudioClicked() {
        audioFilterEnabled.value = audioFilterEnabled.value!!.not()
    }
}