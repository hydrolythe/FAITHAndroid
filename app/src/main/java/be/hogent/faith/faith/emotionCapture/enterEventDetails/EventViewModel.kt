package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.graphics.Bitmap
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.Event
import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.util.LoadingViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import org.koin.core.KoinComponent
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class EventViewModel(
    givenEvent: Event? = null
) : LoadingViewModel(), KoinComponent {

    /**
     * The event that will be discussed and explained using audio, video, drawings,...
     * Updates to the [eventTitle], [eventDate] and [eventNotes] are automatically applied to the event.
     */
    val event = MediatorLiveData<Event>()

    val eventDetails: LiveData<List<Detail>> = Transformations.map(event) { it.details }

    /**
     * The title of the event.
     * It's optional on the main event screen, but has to be filled in when saving the event.
     */
    val eventTitle = MutableLiveData<String>()

    /**
     * The title of the event (optional when on the main entry screen.
     */
    val eventDate = MutableLiveData<LocalDateTime>()

    val eventDateString: LiveData<String> =
        Transformations.map(eventDate) { date ->
            date.format(DateTimeFormatter.ofPattern("dd,MMM yyyy"))
        }

    /**
     * The notes that are added when saving the event
     */
    val eventNotes = MutableLiveData<String>()

    private val _photoSavedSuccessFully = SingleLiveEvent<Int>()
    val photoDetailSavedSuccessFully: LiveData<Int> = _photoSavedSuccessFully

    private val _drawingSavedSuccessFully = SingleLiveEvent<Int>()
    val drawingDetailSavedSuccessFully: LiveData<Int> = _drawingSavedSuccessFully

    private val _audioSavedSuccessFully = SingleLiveEvent<Int>()
    val audioDetailSavedSuccessFully: LiveData<Int> = _audioSavedSuccessFully

    private val _textSavedSuccessFully = SingleLiveEvent<Int>()
    val textDetailSavedSuccessFully: LiveData<Int> = _textSavedSuccessFully

    private val _avatarSavedSuccessFully = SingleLiveEvent<Int>()
    val avatarSavedSuccessFully: LiveData<Int> = _avatarSavedSuccessFully

    private val _detailDeletedSuccessFully = SingleLiveEvent<Int>()
    val detailDeletedSuccessFully: LiveData<Int> = _detailDeletedSuccessFully

    private val _sendButtonClicked = SingleLiveEvent<Unit>()
    val sendButtonClicked: LiveData<Unit> = _sendButtonClicked

    private val _deleteEnabled = MutableLiveData<Boolean>().apply { value = false }
    val deleteEnabled: LiveData<Boolean> = _deleteEnabled

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    init {
        if (givenEvent != null) {
            setEvent(givenEvent)
        } else {
            setEvent(Event())
        }
        eventDate.value = LocalDateTime.now()

        event.addSource(eventTitle) { title -> event.value?.title = title }
        event.addSource(eventDate) { dateTime -> event.value?.dateTime = dateTime }
        event.addSource(eventNotes) { notes -> event.value?.notes = notes }
    }

    fun setEvent(newEvent: Event) {
        event.value = newEvent
        // We have to manually update the related streams for the event MediatorLiveData.
        // Updating the event value won't update the incoming streams, it only works one way.
        eventTitle.postValue(newEvent.title)
        eventNotes.postValue(newEvent.notes)
        eventDate.postValue(newEvent.dateTime)
    }

    private val _cameraButtonClicked = SingleLiveEvent<Unit>()
    val cameraButtonClicked: LiveData<Unit> = _cameraButtonClicked

    private val _textButtonClicked = SingleLiveEvent<Unit>()
    val textButtonClicked: LiveData<Unit> = _textButtonClicked

    private val _audioButtonClicked = SingleLiveEvent<Unit>()
    val audioButtonClicked: LiveData<Unit> = _audioButtonClicked

    private val _drawingButtonClicked = SingleLiveEvent<Unit>()
    val drawingButtonClicked: LiveData<Unit> = _drawingButtonClicked

    private val _emotionAvatarClicked = SingleLiveEvent<Unit>()
    val emotionAvatarClicked: LiveData<Unit> = _emotionAvatarClicked

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit> = _cancelButtonClicked

    private val _backButtonClicked = SingleLiveEvent<Unit>()
    val backButtonClicked: LiveData<Unit> = _backButtonClicked

    private val _dateButtonClicked = SingleLiveEvent<Unit>()
    val dateButtonClicked: LiveData<Unit> = _dateButtonClicked

    /**
     * Helper method to be called when changing one of the properties of the [Event].
     * This is needed because just changing properties doesn't call all subscribers, only changing the actual value does.
     */
    fun updateEvent() {
        event.postValue(event.value)
    }

    fun onCameraButtonClicked() {
        _deleteEnabled.value = false
        _cameraButtonClicked.call()
    }

    fun onTextButtonClicked() {
        _deleteEnabled.value = false
        _textButtonClicked.call()
    }

    fun onAudioButtonClicked() {
        _deleteEnabled.value = false
        _audioButtonClicked.call()
    }

    fun onDrawingButtonClicked() {
        _deleteEnabled.value = false
        _drawingButtonClicked.call()
    }

    fun onCancelButtonClicked() {
        _deleteEnabled.value = false
        _cancelButtonClicked.call()
    }

    fun onBackButtonClicked() {
        _deleteEnabled.value = false
        _backButtonClicked.call()
    }

    fun onDateButtonClicked() {
        _dateButtonClicked.call()
    }

    fun onEmotionAvatarClicked() {
        _deleteEnabled.value = false
        _emotionAvatarClicked.call()
    }

    fun onTrashcanClicked() {
        _deleteEnabled.value = _deleteEnabled.value!!.not()
    }

    fun onSendButtonClicked() {
        _deleteEnabled.value = false
        _sendButtonClicked.call()
    }

    /**
     * Save avatarName bitmap. This updates the property emotionAvatar. Must be done in this viewmodel
     * because otherwise the event is not updated (if this code is in DrawEmotionViewModel, then the
     * fragment needs to update the event in EventViewModel, but the fragment is already stopped before
     * the use case to save the image is done
     */
    fun saveEmotionAvatarImage(bitmap: Bitmap) {
        startLoading()

    }

    fun saveAudioDetail(audioDetail: AudioDetail) {
        startLoading()

    }

    fun savePhotoDetail(photoDetail: PhotoDetail) {
        startLoading()

    }

    fun saveTextDetail(detail: TextDetail) {
        startLoading()

    }

    fun saveDrawingDetail(detail: DrawingDetail) {
        startLoading()

    }

    fun deleteDetail(detail: Detail) {
        startLoading()

    }


}
