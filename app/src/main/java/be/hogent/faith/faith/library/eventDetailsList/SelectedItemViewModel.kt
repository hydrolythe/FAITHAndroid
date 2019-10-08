package be.hogent.faith.faith.library.eventDetailsList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedItemViewModel : ViewModel() {


    /**
     * The index of the detail selected of the event
     */
    var selectedItem = MutableLiveData<Int>()


}