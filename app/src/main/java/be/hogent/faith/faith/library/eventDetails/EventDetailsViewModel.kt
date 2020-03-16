package be.hogent.faith.faith.library.eventDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.SingleLiveEvent
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class EventDetailsViewModel : ViewModel() {
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
        _event.value = event
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }
}