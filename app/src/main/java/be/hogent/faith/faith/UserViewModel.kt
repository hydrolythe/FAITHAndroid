package be.hogent.faith.faith

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.SaveEventUseCase
import io.reactivex.observers.DisposableCompletableObserver

/**
 * Represents the [ViewModel] for the [User] throughout the the application.
 * It should be injected only using the scope specified in the KoinModules file.
 */
class UserViewModel(
    private val saveEventUseCase: SaveEventUseCase
) : ViewModel() {

    private val _eventSavedSuccessFully = SingleLiveEvent<Unit>()
    val eventSavedSuccessFully: LiveData<Unit>
        get() = _eventSavedSuccessFully

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun setUser(user: User) {
        _user.value = user
    }

    fun saveEvent(eventTitle: String?, event: Event) {
        if (eventTitle.isNullOrEmpty()) {
            _errorMessage.postValue(R.string.error_event_no_title)
            return
        }
        val params = SaveEventUseCase.Params(eventTitle, event, user.value!!)
        saveEventUseCase.execute(params, SaveEventUseCaseHandler())
    }

    private inner class SaveEventUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _eventSavedSuccessFully.call()
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_event_failed)
        }
    }

    override fun onCleared() {
        saveEventUseCase.dispose()
        super.onCleared()
    }
}
