package be.hogent.faith.faith.library.eventDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.event.MakeEventFilesAvailableUseCase
import io.reactivex.observers.DisposableSingleObserver
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import timber.log.Timber

class EventDetailsViewModel(
    private val makeEventFilesAvailableUseCase: MakeEventFilesAvailableUseCase
) : ViewModel() {
    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    val eventDate = Transformations.map(_event) {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        formatter.format(it.dateTime)
    }

    val avatarImage = Transformations.map(_event) {
        it.emotionAvatar
    }

    val details = Transformations.map(_event) {
        it.details
    }

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit> = _cancelButtonClicked

    fun setEvent(event: Event) {
        // Set the event already so at least the data is shown
        _event.value = event
        // Then start the use case so files will be loaded
        makeEventFilesAvailableUseCase.execute(
            MakeEventFilesAvailableUseCase.Params(event),
            object :
                DisposableSingleObserver<Event>() {
                override fun onSuccess(t: Event) {
                    Timber.i("Loaded new event files")
                    _event.value = t
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                    Timber.e("Failed to load event")
                }
            })
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    override fun onCleared() {
        super.onCleared()
        makeEventFilesAvailableUseCase.dispose()
    }
}