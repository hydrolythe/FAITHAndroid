package be.hogent.faith.faith.library.eventDetails

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import timber.log.Timber

class EventDetailsViewModel(

) : ViewModel() {
    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int> = _errorMessage

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

    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

}