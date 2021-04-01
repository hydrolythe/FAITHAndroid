package be.hogent.faith.faith

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.NetworkError
import be.hogent.faith.faith.models.Event
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.util.LoadingViewModel
import be.hogent.faith.faith.util.state.Resource
import be.hogent.faith.faith.util.state.ResourceState
import be.hogent.faith.util.TAG
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * Represents the [ViewModel] for the [User] throughout the the application.
 * It should be injected only using the scope specified in the KoinModules file.
 */
class UserViewModel(val userRepository:IUserRepository) : LoadingViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


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

    private val _tokenMessage = MutableLiveData<TokenResult>()
    val tokenMessage: LiveData<TokenResult>
    get() = _tokenMessage

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

    fun getLoggedInUser(string:String?) {
        _getLoggedInUserState.postValue(Resource(ResourceState.LOADING, null, null))
        startLoading()
        runBlocking {
            _tokenMessage.value = userRepository.login(string)
        }
        uiScope.launch {
            val execution = userRepository.getUser()
            if(execution?.success!=null){
                _user.value = execution.success
                doneLoading()
            }
            if(execution?.exception!=null){
                _getLoggedInUserState.value =
                    Resource(
                        ResourceState.ERROR, null,
                        when (execution.exception) {
                            is NetworkError -> R.string.login_error_internet
                            else -> R.string.getLoggedInUser_error
                        }
                    )
                doneLoading()
            }
            _getLoggedInUserState.value = Resource(ResourceState.SUCCESS,Unit,null)
        }
    }

    fun createUser(){
        uiScope.launch {
            val execution = userRepository.getUser()
            if(execution?.success!=null){
                _user.value = execution.success
                doneLoading()
            }
            if(execution?.exception!=null){
                _getLoggedInUserState.value =
                    Resource(
                        ResourceState.ERROR, null,
                        when (execution.exception) {
                            is NetworkError -> R.string.login_error_internet
                            else -> R.string.getLoggedInUser_error
                        }
                    )
                doneLoading()
            }
            _getLoggedInUserState.value = Resource(ResourceState.SUCCESS,Unit,null)
        }
    }

    fun saveEvent(event: Event) {
        if (event.title.isNullOrEmpty()) {
            _titleErrorMessage.postValue(R.string.error_event_no_title)
            return
        }
        _eventSavedState.postValue(Resource(ResourceState.LOADING, null, null))

    }

    fun clearErrorMessage() {
        _titleErrorMessage.postValue(R.string.empty)
    }
}
