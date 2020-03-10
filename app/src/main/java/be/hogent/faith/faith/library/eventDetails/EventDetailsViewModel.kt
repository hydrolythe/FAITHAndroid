package be.hogent.faith.faith.library.eventDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventDetailsViewModel : ViewModel() {

    /**
     * The index of the detail selected of the event
     */
    var selectedItem = MutableLiveData<Int>()
}