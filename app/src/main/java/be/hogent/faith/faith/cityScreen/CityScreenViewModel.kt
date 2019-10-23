package be.hogent.faith.faith.cityScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.LogoutUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableCompletableObserver
import timber.log.Timber

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
            Timber.e( "logout failed")
        }
    }

    override fun onCleared() {
        logoutUserUseCase.dispose()
        super.onCleared()
    }
}