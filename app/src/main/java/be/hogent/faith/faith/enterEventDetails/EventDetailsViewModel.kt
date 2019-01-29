package be.hogent.faith.faith.enterEventDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventDetailsViewModel : ViewModel() {
    /**
     * The title of the event (optional when on the main entry screen)
     */
    val eventTitle = MutableLiveData<String>()


    fun onCameraButtonClicked() {

    }

    fun onTextButtonClicked() {

    }

    fun onAudioButtonClicked() {

    }

    fun onDrawingButtonClicked() {

    }

    fun onSendButtonClicked() {

    }

}