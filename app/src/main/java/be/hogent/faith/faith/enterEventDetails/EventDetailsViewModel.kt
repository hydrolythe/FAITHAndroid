package be.hogent.faith.faith.enterEventDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import java.util.UUID

class EventDetailsViewModel(val user: LiveData<User>, eventUuid: UUID? = null) : ViewModel() {

    /**
     * The event that will be discussed and explained using audio, video, drawings,...
     */
    val event = MutableLiveData<Event>()

    /**
     * The title of the event (optional when on the main entry screen.
     * We can't observe it here, so the Fragment observes it and changes the [event] when required.
     */
    val eventTitle = MutableLiveData<String>()

    init {
        if (eventUuid != null) {
            // Recreating an existing event
            val result = user.value?.getEvent(eventUuid)
            event.postValue(result)
            eventTitle.postValue(result?.title)
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

    init {
        event.value = Event()
    }

    /**
     * Helper method to be called when changing one of the properties of the [event].
     * This is needed because just changing properties doesn't call all subscribers, only changing the actual value does.
     */
    fun updateEvent() {
        event.value = event.value
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

    fun onEmotionAvatarClicked() {
        _emotionAvatarClicked.call()
    }
}
