package be.hogent.faith.faith.emotionCapture.enterEventDetails

import android.graphics.Bitmap
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.service.usecases.SaveEventAudioUseCase
import be.hogent.faith.service.usecases.SaveEventDrawingUseCase
import be.hogent.faith.service.usecases.SaveEventPhotoUseCase
import be.hogent.faith.service.usecases.CreateTextDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.io.File

class EventViewModel(
    private val saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase,
    private val saveEventPhotoUseCase: SaveEventPhotoUseCase,
    private val saveEventAudioUseCase: SaveEventAudioUseCase,
    private val saveEventDrawingUseCase: SaveEventDrawingUseCase,
    private val createTextDetailUseCase: CreateTextDetailUseCase,
    givenEvent: Event? = null
) : ViewModel(), KoinComponent {

    private val fileProvider: TempFileProvider by inject()
    /**
     * The event that will be discussed and explained using audio, video, drawings,...
     * Updates to the [eventTitle], [eventDate] and [eventNotes] are automatically applied to the event.
     */
    val event = MediatorLiveData<Event>()

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
        Transformations.map<LocalDateTime, String>(eventDate) { date ->
            date.format(DateTimeFormatter.ISO_DATE)
        }

    /**
     * The notes that are added when saving the event
     */
    val eventNotes = MutableLiveData<String>()

    private val _photoSavedSuccessFully = SingleLiveEvent<Detail>()
    val photoSavedSuccessFully: LiveData<Detail>
        get() = _photoSavedSuccessFully

    private val _drawingSavedSuccessFully = SingleLiveEvent<Unit>()
    val drawingSavedSuccessFully: LiveData<Unit>
        get() = _drawingSavedSuccessFully

    private val _recordingSavedSuccessFully = SingleLiveEvent<Unit>()
    val recordingSavedSuccessFully: LiveData<Unit>
        get() = _recordingSavedSuccessFully

    private val _textSavedSuccessFully = SingleLiveEvent<Unit>()
    val textSavedSuccessFully: LiveData<Unit>
        get() = _textSavedSuccessFully

    private val _avatarSavedSuccessFully = SingleLiveEvent<Unit>()
    val avatarSavedSuccessFully: LiveData<Unit>
        get() = _avatarSavedSuccessFully

    private val _sendButtonClicked = SingleLiveEvent<Unit>()
    val sendButtonClicked: LiveData<Unit>
        get() = _sendButtonClicked

    fun onSendButtonClicked() {
        _sendButtonClicked.call()
    }

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

    /**
     * verhuisd naar EditDetail
    private val _sendButtonClicked = SingleLiveEvent<Unit>()
    val sendButtonClicked: LiveData<Unit>
    get() = _sendButtonClicked
     */

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

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun onDateButtonClicked() {
        _dateButtonClicked.call()
    }

    /**
     * Used to reset the ViewModel once an Event is saved.
     * This will allow the ViewModel to be reused for a new event.
     */
    // TODO: check if still needed?
    fun resetViewModel() {
        event.postValue(Event())
        eventDate.postValue(LocalDateTime.now())
        eventTitle.postValue(null)
        eventNotes.postValue(null)
    }

    fun onEmotionAvatarClicked() {
        _emotionAvatarClicked.call()
    }

    //region saveEmotionAvatar
    /**
     * Save avatarName bitmap. This updates the property emotionAvatar. Must be done in this viewmodel
     * because otherwise the event is not updated (if this code is in DrawEmotionViewModel, then the
     * fragment needs to update the event in EventViewModel, but the fragment is already stopped before
     * the use case to save the image is done
     */
    fun saveEmotionAvatarImage(bitmap: Bitmap) {
        val params = SaveEmotionAvatarUseCase.Params(bitmap, event.value!!)
        saveEmotionAvatarUseCase.execute(params, SaveEmotionAvatarUseCaseHandler())
    }

    private inner class SaveEmotionAvatarUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            updateEvent()
            _avatarSavedSuccessFully.value = Unit
        }

        override fun onError(e: Throwable) {
            Timber.e(e.localizedMessage)
            _errorMessage.postValue(R.string.error_save_avatar_failed)
        }
    }
    //endregion

    //region saveAudio
    fun saveAudio() {
        val params = SaveEventAudioUseCase.Params(
            fileProvider.tempAudioRecordingFile,
            event.value!!
        )
        saveEventAudioUseCase.execute(params, SaveAudioUseCaseHandler())
    }

    private inner class SaveAudioUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _recordingSavedSuccessFully.call()
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_audio_failed)
        }
    }
    //endregion

    //region savePhoto
    fun savePhoto(tempPhotoFile: File) {
        val params = SaveEventPhotoUseCase.Params(tempPhotoFile, event.value!!)
        saveEventPhotoUseCase.execute(params, TakeEventPhotoUseCaseHandler())
    }

    private inner class TakeEventPhotoUseCaseHandler : DisposableSingleObserver<Detail>() {
        override fun onSuccess(savedDetail: Detail) {
            _photoSavedSuccessFully.value = savedDetail
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_photo_failed)
        }
    }
    //endregion

    //region saveDrawing
    fun saveDrawing(detail: DrawingDetail) {
        val params = SaveEventDrawingUseCase.Params(detail, event.value!!)
        saveEventDrawingUseCase.execute(params, SaveEventDrawingUseCaseHandler())
    }

    private inner class SaveEventDrawingUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _drawingSavedSuccessFully.call()
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_drawing_failed)
        }
    }
    //endregion

    override fun onCleared() {
        saveEventAudioUseCase.dispose()
        saveEventPhotoUseCase.dispose()
        saveEmotionAvatarUseCase.dispose()
        saveEventDrawingUseCase.dispose()
        createTextDetailUseCase.dispose()
        super.onCleared()
    }
}
