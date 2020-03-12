package be.hogent.faith.faith.library.eventList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.library.eventfilters.CombinedEventFilter
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import io.reactivex.subscribers.DisposableSubscriber
import org.threeten.bp.LocalDate
import java.util.UUID

class EventListViewModel(
    user: User,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {
    // Currently like this to do testing
    private var events: List<Event> = emptyList()

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

    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    init {
        loadEvents(user)
    }

    private fun loadEvents(user: User) {
        getEventsUseCase.execute(GetEventsUseCase.Params(user), GetEventsUseCaseHandler())
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

    fun getEvent(eventUuid: UUID): Event? {
        return events.find { it.uuid == eventUuid }
    }

    private inner class GetEventsUseCaseHandler : DisposableSubscriber<List<Event>>() {

        override fun onComplete() {
            // NOP
        }

        override fun onNext(t: List<Event>) {
            events = t
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_load_events)
        }
    }
}