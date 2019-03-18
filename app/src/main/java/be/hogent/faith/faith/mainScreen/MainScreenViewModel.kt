package be.hogent.faith.faith.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

/**
 * ViewModel for the [MainScreenFragment].
 */
class MainScreenViewModel : ViewModel() {

    val firstLocation = SingleLiveEvent<Unit>()
    val secondLocation = SingleLiveEvent<Unit>()
    val thirdLocation = SingleLiveEvent<Unit>()

    fun firstLocationClicked() {
        Log.i(TAG, "First location clicked")
        firstLocation.call()
    }
    fun secondLocationClicked() {
        Log.i(TAG, "Second location clicked")
        secondLocation.call()
    }

    fun thirdLocationClicked() {
        Log.i(TAG, "third location clicked")
        thirdLocation.call()
    }

    companion object {
        const val TAG = "MainScreenViewModel"
    }
}