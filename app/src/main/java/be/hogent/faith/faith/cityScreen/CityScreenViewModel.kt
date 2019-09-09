package be.hogent.faith.faith.cityScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.util.TAG

/**
 * ViewModel for the [CityScreenFragment].
 */
class CityScreenViewModel : ViewModel() {

    val archiveClicked = SingleLiveEvent<Unit>()
    val parkClicked = SingleLiveEvent<Unit>()
    val thirdLocation = SingleLiveEvent<Unit>()
    val logOutClicked = SingleLiveEvent<Unit>()

    fun onArchiveClicked() {
        Log.i(TAG, "First location clicked")
        archiveClicked.call()
    }

    fun onParkClicked() {
        Log.i(TAG, "Second location clicked")
        parkClicked.call()
    }

    fun thirdLocationClicked() {
        Log.i(TAG, "third location clicked")
        thirdLocation.call()
    }

    // TODO: link to button
    fun onLogOutClicked() {
        logOutClicked.call()
    }
}