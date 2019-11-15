package be.hogent.faith.faith

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.NetworkError
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.service.usecases.SaveEventUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber
import timber.log.Timber

/**
 * Represents the [ViewModel] for the [User] throughout the the application.
 * It should be injected only using the scope specified in the KoinModules file.
 */
class UserViewModel(
    private val saveEventUseCase: SaveEventUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private var userName: String? = null
    private var password: String? = null
    private var avatar: String? = null

    private val _eventSavedState = MutableLiveData<Resource<Unit>>()
    val eventSavedState: LiveData<Resource<Unit>>
        get() = _eventSavedState

    private val _getLoggedInUserState = MutableLiveData<Resource<Unit>>()
    val getLoggedInUserState: LiveData<Resource<Unit>>
        get() = _getLoggedInUserState

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private var _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _titleErrorMessage = MutableLiveData<Int>()
    val titleErrorMessage: LiveData<Int>
        get() = _titleErrorMessage

    fun setUser(user: User) {
        _user.postValue(user)
    }

    /*
    private val _userRegisteredSuccessFully = SingleLiveEvent<Unit>()
    val UserRegisteredSuccessFully: LiveData<Unit>
        get() = _userRegisteredSuccessFully
*/
    private fun userNameIsValid(): Boolean {
        if (userName.isNullOrBlank()) {
            return false
        }
        return true
    }

    private fun passwordIsValid(): Boolean {
        if (password.isNullOrBlank())
            return false
        return true
    }

    fun getLoggedInUser() {
        _getLoggedInUserState.postValue(Resource(ResourceState.LOADING, null, null))
        getUserUseCase.execute(null, GetUserUseCaseHandler())
    }

    private inner class GetUserUseCaseHandler : DisposableSubscriber<User>() {

        override fun onNext(t: User?) {
            Timber.i(TAG, "success $t")
            _user.postValue(t)
            _getLoggedInUserState.postValue(Resource(ResourceState.SUCCESS, Unit, null))
        }

        override fun onComplete() {
            Timber.i(TAG, "completed")
        }

        override fun onError(e: Throwable) {
            Timber.e(TAG, e.localizedMessage)
            _getLoggedInUserState.postValue(
                Resource(
                    ResourceState.ERROR, null,
                    when (e) {
                        is NetworkError -> R.string.login_error_internet
                        else -> R.string.register_error_create_user
                    }
                )
            )
        }
    }

    fun saveEvent(eventTitle: String?, event: Event) {
        if (eventTitle.isNullOrEmpty()) {
            _titleErrorMessage.postValue(R.string.error_event_no_title)
            return
        }
        _eventSavedState.postValue(Resource(ResourceState.LOADING, null, null))
        val params = SaveEventUseCase.Params(eventTitle, event, user.value!!)
        saveEventUseCase.execute(params, SaveEventUseCaseHandler())
    }

    private inner class SaveEventUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _eventSavedState.postValue(Resource(ResourceState.SUCCESS, Unit, null))
        }

        override fun onError(e: Throwable) {
            Timber.e(e)
            _eventSavedState.postValue(
                Resource(
                    ResourceState.ERROR,
                    null,
                    R.string.error_save_event_failed
                )
            )
        }
    }

    override fun onCleared() {
        saveEventUseCase.dispose()
        getUserUseCase.dispose()
        super.onCleared()
    }
}
