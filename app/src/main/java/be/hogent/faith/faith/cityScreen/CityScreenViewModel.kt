package be.hogent.faith.faith.cityScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for the [CityScreenFragment].
 */
class CityScreenViewModel(val cityScreenRepository: CityScreenRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val libraryClicked = SingleLiveEvent<Unit>()
    val parkClicked = SingleLiveEvent<Unit>()
    val treehouseClicked = SingleLiveEvent<Unit>()
    val backpackClicked = SingleLiveEvent<Unit>()
    val cinemaClicked = SingleLiveEvent<Unit>()
    val skyscraperClicked = SingleLiveEvent<Unit>()
    val feedbackClicked = SingleLiveEvent<Unit>()

    private val _logoutSuccessFull = SingleLiveEvent<Unit>()
    val logoutSuccessFull: LiveData<Unit>
        get() = _logoutSuccessFull

    fun feedbackButtonClicked() {
        feedbackClicked.call()
    }

    fun onArchiveClicked() {
        libraryClicked.call()
    }

    fun onParkClicked() {
        parkClicked.call()
    }

    fun onTreehouseClicked() {
        treehouseClicked.call()
    }

    fun onBackpackClicked() {
        backpackClicked.call()
    }

    fun onCinemaClicked() {
        cinemaClicked.call()
    }

    fun onSkyscraperClicked() {
        Timber.i("Sixth location clicked")
        skyscraperClicked.call()
    }

    fun onLogOutClicked() {
        logout()
    }

    private fun logout() {
        uiScope.launch {
            val result = cityScreenRepository.logout()
            if(result.success!=null){
                _logoutSuccessFull.call()
            }
        }
    }
}