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
import be.hogent.faith.service.usecases.TakeEventPhotoUseCase
import io.reactivex.observers.DisposableCompletableObserver
import java.io.File

/**
 * Represents the [ViewModel] for the [User] throughout the the application.
 * It should be injected only using the scope specified in the KoinModules file.
 */
class UserViewModel(
    private val saveEventUseCase: SaveEventUseCase,
    private val takeEventPhotoUseCase: TakeEventPhotoUseCase
) : ViewModel() {

    private val _eventSavedSuccessFully = SingleLiveEvent<Unit>()
    val eventSavedSuccessFully: LiveData<Unit>
        get() = _eventSavedSuccessFully

    private val _photoSavedSuccessFully = SingleLiveEvent<Unit>()
    val photoSavedSuccessFully: LiveData<Unit>
        get() = _photoSavedSuccessFully

    /**
     * Will be updated with the latest error message when an error occurs
     */
    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private var _user = MutableLiveData<User>()

    /**
     * Returns a LiveData object for the [User].
     */
    val user: LiveData<User>
        get() = _user

    /**
     * Sets the [User] for this ViewModel.
     */
    fun setUser(user: User) {
        _user.postValue(user)
    }

    fun savePhoto(tempPhotoFile: File, event: Event) {
        // TODO: remove name from UC when it's not necessary anymore
        val params = TakeEventPhotoUseCase.Params(tempPhotoFile, event, "TempPhotoName")
        takeEventPhotoUseCase.execute(params, TakeEventPhotoUseCaseHandler())
    }

    private inner class TakeEventPhotoUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _photoSavedSuccessFully.value = Unit
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_photo_failed)
        }
    }


    fun saveEvent(eventTitle: String?, event: Event) {
        if (eventTitle.isNullOrEmpty()) {
            _errorMessage.postValue(R.string.error_event_no_title)
            return
        }
        val params = SaveEventUseCase.Params(event, user.value!!)
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