package be.hogent.faith.faith.cityScreen

import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import timber.log.Timber

/**
 * ViewModel for the [CityScreenFragment].
 */
class CityScreenViewModel : ViewModel() {

    val archiveClicked = SingleLiveEvent<Unit>()
    val parkClicked = SingleLiveEvent<Unit>()
    val thirdLocation = SingleLiveEvent<Unit>()
    val logOutClicked = SingleLiveEvent<Unit>()

    fun onArchiveClicked() {
        Timber.i("First location clicked")
        archiveClicked.call()
    }

    fun onParkClicked() {
        Timber.i("Second location clicked")
        parkClicked.call()
    }

    fun thirdLocationClicked() {
        Timber.i("third location clicked")
        thirdLocation.call()
    }

    // TODO: link to button
    fun onLogOutClicked() {
        logOutClicked.call()
    }
}