package be.hogent.faith.faith.library.eventList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.library.eventfilters.CombinedEventFilter
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.event.GetEventsUseCase
import io.reactivex.subscribers.DisposableSubscriber
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.UUID

class EventListViewModel(
    user: User,
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {
    // Currently like this to do testing
    private var events: List<Event> = emptyList()

    private val eventFilter = CombinedEventFilter()

    val searchString = MutableLiveData<String>()

    private val _startDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.MIN.plusDays(1)
    }
    val startDate: LiveData<LocalDate>
        get() = _startDate

    private val _endDate = MutableLiveData<LocalDate>().apply {
        this.value = LocalDate.now()
    }

    val endDate: LiveData<LocalDate>
        get() = _endDate

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
    val deleteEnabled = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    private var _startDateClicked = SingleLiveEvent<Unit>()
    val startDateClicked: LiveData<Unit> = _startDateClicked

    private var _endDateClicked = SingleLiveEvent<Unit>()
    val endDateClicked: LiveData<Unit> = _endDateClicked

    val dateRangeString: LiveData<String> = MediatorLiveData<String>().apply {
        this.addSource(startDate) { _ -> value = combineLatestDates(startDate, endDate) }
        this.addSource(endDate) { _ -> value = combineLatestDates(startDate, endDate) }
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

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit> = _cancelButtonClicked

    init {
        loadEvents(user)
    }

    private fun loadEvents(user: User) {
        getEventsUseCase.execute(GetEventsUseCase.Params(user),
            object : DisposableSubscriber<List<Event>>() {
                override fun onComplete() {}

                override fun onNext(t: List<Event>) {
                    events = t.sortedByDescending { it.dateTime }
                    audioFilterEnabled.value =
                        audioFilterEnabled.value // anders wordt de lijst niet getoond
                }

                override fun onError(e: Throwable) {
                    _errorMessage.postValue(R.string.error_load_events)
                }
            })
    }

    private fun combineLatestDates(
        startDate: LiveData<LocalDate>,
        endDate: LiveData<LocalDate>
    ): String {
        val van =
            if (startDate.value == LocalDate.MIN.plusDays(1)) "van" else startDate.value!!.format(
                DateTimeFormatter.ISO_DATE
            )
        val tot = endDate.value!!.format(DateTimeFormatter.ISO_DATE)
        return "$van - $tot"
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

    fun onDeleteClicked() {
        deleteEnabled.value = deleteEnabled.value!!.not()
    }

    fun onStartDateClicked() {
        _startDateClicked.call()
    }

    fun onEndDateClicked() {
        _endDateClicked.call()
    }

    fun getEvent(eventUuid: UUID): Event? {
        return events.find { it.uuid == eventUuid }
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun setDateRange(startDate: Long?, endDate: Long?) {
        _startDate.value =
            if (startDate != null) toLocalDate(startDate) else LocalDate.MIN.plusDays(1)
        _endDate.value = if (endDate != null) toLocalDate(endDate) else LocalDate.now()
    }

    fun deleteEvent(eventUUID: UUID) {
        // TODO must be implemented
        Timber.d("Item deleted ${eventUUID}")
    }

    private fun toLocalDate(milliseconds: Long): LocalDate? {
        return Instant.ofEpochMilli(milliseconds) // Convert count-of-milliseconds-since-epoch into a date-time in UTC (`Instant`).
            .atZone(ZoneId.of("Europe/Brussels")) // Adjust into the wall-clock time used by the people of a particular region (a time zone). Produces a `ZonedDateTime` object.
            .toLocalDate()
    }
}