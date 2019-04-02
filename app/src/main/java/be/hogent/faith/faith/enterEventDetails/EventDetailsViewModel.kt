package be.hogent.faith.faith.enterEventDetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.service.usecases.SaveEventUseCase
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class EventDetailsViewModel(
    private val saveEventUseCase: SaveEventUseCase,
    val user: LiveData<User>
) : ViewModel() {

    /**
     * The event that will be discussed and explained using audio, video, drawings,...
     */
    val event = MutableLiveData<Event>()

    /**
     * The title of the event.
     * It's optional on the main event screen, but has to be filled in when saving the event.
     * We can't observe it here, so the Fragment observes it and changes the [event] when required.
     */
    val eventTitle = MutableLiveData<String>()

    /**
     * The title of the event (optional when on the main entry screen.
     * We can't observe it here, so the Fragment observes it and changes the [event] when required.
     */
    val eventDate = MutableLiveData<LocalDateTime>()

    val eventDateString: LiveData<String> = Transformations.map<LocalDateTime, String>(eventDate) { date ->
        date.format(DateTimeFormatter.ISO_DATE)
    }

    /**
     * The notes that are added when saving the event
     * We can't observe it here, so the Fragment observes it and changes the [event] when required.
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


    init {
        eventDate.value = LocalDateTime.now()

    }

    fun setEvent(eventUUID: UUID) {
        user.value!!.getEvent(eventUUID)?.let {
            event.postValue(it)
            eventTitle.postValue(it.title)
            eventNotes.postValue(it.notes)
        }
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

    init {
        event.value = Event()
    }

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
        if (eventTitle.value!!.isEmpty()) {
            _inputErrorMessageID.value = R.string.toast_event_no_title
            return
        }

        val params = SaveEventUseCase.Params(event.value!!, user.value!!)
        val disposable = saveEventUseCase.execute(params).subscribe({
            Log.i(TAG, "New event saved: ${event.value!!.title}")
            _eventSavedSuccessFully.call()
        }, {
            Log.i(TAG, "Event failed to save because: ${it.message}")
            _eventSaveFailed.postValue(it.localizedMessage)
        })
        disposables.add(disposable)
    }

    fun onEmotionAvatarClicked() {
        _emotionAvatarClicked.call()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
