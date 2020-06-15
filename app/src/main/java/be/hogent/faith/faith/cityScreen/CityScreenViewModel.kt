package be.hogent.faith.faith.cityScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.user.LogoutUserUseCase
import io.reactivex.observers.DisposableCompletableObserver
import timber.log.Timber

/**
 * ViewModel for the [CityScreenFragment].
 */
class CityScreenViewModel(private val logoutUserUseCase: LogoutUserUseCase) : ViewModel() {

    val libraryClicked = SingleLiveEvent<Unit>()
    val parkClicked = SingleLiveEvent<Unit>()
    val treehouseClicked = SingleLiveEvent<Unit>()
    val backpackClicked = SingleLiveEvent<Unit>()
    val cinemaClicked = SingleLiveEvent<Unit>()
    val skyscraperClicked = SingleLiveEvent<Unit>()

    private val _logoutSuccessFull = SingleLiveEvent<Unit>()
    val logoutSuccessFull: LiveData<Unit>
        get() = _logoutSuccessFull

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
        logoutUserUseCase.execute(null, LogoutUserUseCaseHandler())
    }

    private inner class LogoutUserUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _logoutSuccessFull.call()
        }

        override fun onError(e: Throwable) {
            Timber.e("logout failed")
        }
    }

    override fun onCleared() {
        logoutUserUseCase.dispose()
        super.onCleared()
    }
}