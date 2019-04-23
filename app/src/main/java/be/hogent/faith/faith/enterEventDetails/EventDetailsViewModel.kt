package be.hogent.faith.faith.enterEventDetails

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import io.reactivex.disposables.CompositeDisposable
import java.util.UUID

class EventDetailsViewModel(private val saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase, val user: LiveData<User>, eventUuid: UUID? = null) : ViewModel() {

    /**
     * The event that will be discussed and explained using audio, video, drawings,...
     */
    val event = MutableLiveData<Event>()

    /**
     * The title of the event (optional when on the main entry screen.
     * We can't observe it here, so the Fragment observes it and changes the [event] when required.
     */
    val eventTitle = MutableLiveData<String>()

    /**
     * The errormessages
     */
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _avatarSavedSuccessFully = SingleLiveEvent<Unit>()
    val avatarSavedSuccessFully: LiveData<Unit>
        get() = _avatarSavedSuccessFully

    private val disposables = CompositeDisposable()

    init {
        if (eventUuid != null) {
            // Recreating an existing event
            val result = user.value?.getEvent(eventUuid)
            event.postValue(result)
            eventTitle.postValue(result?.title)
        } else
            event.value = Event()
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

    /**
     * Used to reset the ViewModel once an Event is saved.
     * This will allow the ViewModel to be reused for a new event.
     */
    fun resetViewModel() {
        event.postValue(Event())
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    /**
     * Save avatar bitmap. This updates the property emotionAvatar. Must be done in this viewmodel
     * because otherwise the event is not updated (if this code is in DrawEmotionViewModel, then the
     * fragment needs to update the event in EventDetailsViewModel, but the fragment is already stopped before
     * the use case to save the image is done
     */
    fun saveImage(bitmap: Bitmap) {
        val saveRequest = saveEmotionAvatarUseCase.execute(
            SaveEmotionAvatarUseCase.Params(bitmap, event.value!!)
        ).subscribe({
            updateEvent()
            _avatarSavedSuccessFully.value = Unit
        }, {
            _errorMessage.postValue(it.message)
        }
        )
        disposables.add(saveRequest)
    }
}
