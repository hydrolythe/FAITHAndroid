package be.hogent.faith.faith.cityScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.LogoutUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableCompletableObserver

/**
 * ViewModel for the [CityScreenFragment].
 */
class CityScreenViewModel(private val logoutUserUseCase: LogoutUserUseCase) : ViewModel() {

    val archiveClicked = SingleLiveEvent<Unit>()
    val parkClicked = SingleLiveEvent<Unit>()
    val thirdLocation = SingleLiveEvent<Unit>()

    private val _logoutSuccessFull = SingleLiveEvent<Unit>()
    val logoutSuccessFull: LiveData<Unit>
        get() = _logoutSuccessFull

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

    fun onLogOutClicked() {
        logout()
    }

    private fun logout() {
        logoutUserUseCase.execute(null, LogoutUserUseCaseHandler())
    }

    private inner class LogoutUserUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            Log.i(TAG, "user signed out")
            _logoutSuccessFull.call()
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, "logout failed")
        }
    }
}