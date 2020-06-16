package be.hogent.faith.faith

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.repositories.NetworkError
import be.hogent.faith.service.usecases.event.SaveEventUseCase
import be.hogent.faith.service.usecases.user.GetUserUseCase
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
        get() {
            return _user
        }

    private val _titleErrorMessage = MutableLiveData<Int>()
    val titleErrorMessage: LiveData<Int>
        get() = _titleErrorMessage

    fun setUser(user: User) {
        _user.postValue(user)
    }

    /**
     * if the fragment updated the ui, the state is set to null.
     * reason : a new DetailFragment is created when you want to register a new event, so the state must be empty again
     */
    fun eventSavedHandled() {
        _eventSavedState.postValue(null)
    }

    fun getLoggedInUser() {
        _getLoggedInUserState.postValue(Resource(ResourceState.LOADING, null, null))
        getUserUseCase.execute(GetUserUseCase.Params(), object : DisposableSubscriber<User>() {

            override fun onNext(t: User) {
                Timber.i("success $t")
                _user.postValue(t)
                _getLoggedInUserState.value = Resource(ResourceState.SUCCESS, Unit, null)
            }

            override fun onComplete() {
                Timber.i(TAG, "completed")
            }

            override fun onError(e: Throwable) {
                Timber.e(e)
                _getLoggedInUserState.value =
                    Resource(
                        ResourceState.ERROR, null,
                        when (e) {
                            is NetworkError -> R.string.login_error_internet
                            else -> R.string.getLoggedInUser_error
                        }
                    )
            }
        })
    }

    fun saveEvent(event: Event) {
        if (event.title.isNullOrEmpty()) {
            _titleErrorMessage.postValue(R.string.error_event_no_title)
            return
        }
        _eventSavedState.postValue(Resource(ResourceState.LOADING, null, null))
        val params = SaveEventUseCase.Params(event, user.value!!)
        saveEventUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                Timber.i("Successfully saved event")
                _eventSavedState.value = Resource(ResourceState.SUCCESS, Unit, null)
            }

            override fun onError(e: Throwable) {
                Timber.e(e, "error saving event ${e.localizedMessage}")
                _eventSavedState.value =
                    Resource(
                        ResourceState.ERROR,
                        null,
                        R.string.error_save_event_failed
                    )
            }
        })
    }

    override fun onCleared() {
        saveEventUseCase.dispose()
        getUserUseCase.dispose()
        super.onCleared()
    }

    fun clearErrorMessage() {
        _titleErrorMessage.postValue(R.string.empty)
    }
}
