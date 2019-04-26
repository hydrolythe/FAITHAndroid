package be.hogent.faith.faith.enterEventDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.util.TAG
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class EventDetailsViewModel(
    private val saveEventUseCase: SaveEventUseCase,
    val user: LiveData<User>
) : ViewModel() {

    /**
     * The title of the event.
     * It's optional on the main event screen, but has to be filled in when saving the event.
     */
    val eventTitle = MutableLiveData<String>()

    /**
     * The title of the event (optional when on the main entry screen.
     */
    val eventDate = MutableLiveData<LocalDateTime>()

    val eventDateString: LiveData<String> = Transformations.map<LocalDateTime, String>(eventDate) { date ->
        date.format(DateTimeFormatter.ISO_DATE)
    }

    /**
     * The notes that are added when saving the event
     */
    val eventNotes = MutableLiveData<String>()

    private val disposables = CompositeDisposable()

    private val _eventSavedSuccessFully = SingleLiveEvent<Unit>()
    val eventSavedSuccessFully: LiveData<Unit>
        get() = _eventSavedSuccessFully

    /**
     * Will be updated with the latest error message when an error occurs when saving
     */
    private val _eventSaveFailed = MutableLiveData<String>()
    val eventSaveFailed: LiveData<String>
        get() = _eventSaveFailed

    /**
     * Holds potential error messages that will be displayed to the user when he/she tries to save the event but
     * not enough information was given.
     * Example: trying to save the event but no title was given.
     *
     * Values should be reference to string resources, not the strings themselves.
     */
    private val _inputErrorMessageID = MutableLiveData<Int>()
    val inputErrorMessageID: LiveData<Int>
        get() = _inputErrorMessageID

    /**
     * The event that will be discussed and explained using audio, video, drawings,...
     * Updates to the [eventTitle], [eventDate] and [eventNotes] are automatically applied to the event.
     */
    val event = MediatorLiveData<Event>()

    init {
        event.value = Event()
        eventDate.value = LocalDateTime.now()

        event.addSource(eventTitle) { title -> event.value?.title = title }
        event.addSource(eventDate) { dateTime -> event.value?.dateTime = dateTime }
        event.addSource(eventNotes) { notes -> event.value?.notes = notes }
    }

    fun setEvent(newEventUUID: UUID) {
        val newEvent = user.value!!.getEvent(newEventUUID)
            ?: throw IllegalArgumentException("Couldn't find event with UUID $newEventUUID for user ${user.value}")
        event.postValue(newEvent)
        eventTitle.postValue(newEvent.title)
        eventNotes.postValue(newEvent.notes)
        eventDate.postValue(newEvent.dateTime)
    }

    private val _cameraButtonClicked = SingleLiveEvent<Unit>()
    val cameraButtonClicked: LiveData<Unit>
        get() = _cameraButtonClicked

    private val _textButtonClicked = SingleLiveEvent<Unit>()
    val textButtonClicked: LiveData<Unit>
        get() = _textButtonClicked

    private val _audioButtonClicked = SingleLiveEvent<Unit>()
    val audioButtonClicked: LiveData<Unit>
        get() = _audioButtonClicked

    private val _drawingButtonClicked = SingleLiveEvent<Unit>()
    val drawingButtonClicked: LiveData<Unit>
        get() = _drawingButtonClicked

    private val _sendButtonClicked = SingleLiveEvent<Unit>()
    val sendButtonClicked: LiveData<Unit>
        get() = _sendButtonClicked

    private val _emotionAvatarClicked = SingleLiveEvent<Unit>()
    val emotionAvatarClicked: LiveData<Unit>
        get() = _emotionAvatarClicked

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit>
        get() = _cancelButtonClicked

    private val _dateButtonClicked = SingleLiveEvent<Unit>()
    val dateButtonClicked: LiveData<Unit>
        get() = _dateButtonClicked

    /**
     * Helper method to be called when changing one of the properties of the [Event].
     * This is needed because just changing properties doesn't call all subscribers, only changing the actual value does.
     */
    fun updateEvent() {
        event.postValue(event.value)
    }

    fun onCameraButtonClicked() {
        _cameraButtonClicked.call()
    }

    fun onTextButtonClicked() {
        _textButtonClicked.call()
    }

    fun onAudioButtonClicked() {
        _audioButtonClicked.call()
    }

    fun onDrawingButtonClicked() {
        _drawingButtonClicked.call()
    }

    fun onSendButtonClicked() {
        _sendButtonClicked.call()
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun onDateButtonClicked() {
        _dateButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        if (eventTitle.value.isNullOrEmpty()) {
            _inputErrorMessageID.postValue(R.string.toast_event_no_title)
            return
        }
        val params = SaveEventUseCase.Params(event.value!!, user.value!!)
        // TODO: checken of user livedata ook geupdated wordt als een van zijn events aangepast wordt.
        val disposable = saveEventUseCase.execute(params).subscribe({
            Log.i(TAG, "New event saved: ${event.value!!.title}")
            _eventSavedSuccessFully.call()
            resetViewModel()
        }, {
            Log.i(TAG, "Event failed to save because: ${it.message}")
            _eventSaveFailed.postValue(it.localizedMessage)
        })
        disposables.add(disposable)
    }

    /**
     * Used to reset the ViewModel once an Event is saved.
     * This will allow the ViewModel to be reused for a new event.
     */
    fun resetViewModel() {
        event.postValue(Event())
        eventDate.postValue(LocalDateTime.now())
        eventTitle.postValue(null)
        eventNotes.postValue(null)
    }

    fun onEmotionAvatarClicked() {
        _emotionAvatarClicked.call()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
