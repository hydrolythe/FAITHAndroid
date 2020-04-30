package be.hogent.faith.faith.loginOrRegister

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.loginOrRegister.registerAvatar.Avatar
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.repositories.InvalidCredentialsException
import be.hogent.faith.service.repositories.UserCollisionException
import be.hogent.faith.service.repositories.WeakPasswordException
import be.hogent.faith.service.usecases.user.CreateUserUseCase
import io.reactivex.observers.DisposableCompletableObserver
import timber.log.Timber

/**
 * Represents the [ViewModel] for the user during the registering process.
 * It is shared by the fragments needed for the workflow of registering
 * If all the information (username, password and avatar) is available, the user will be registered
 */
class RegisterUserViewModel(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _avatar = MutableLiveData<Avatar>()

    private val _userRegisteredState = MutableLiveData<Resource<Unit>>()
    val userRegisteredState: LiveData<Resource<Unit>>
        get() = _userRegisteredState

    /**
     * part 1 of the register workflow sets the username and password
     */
    fun setRegistrationDetails(userName: String, password: String) {
        _userName.value = userName
        _password.value = password
    }

    /**
     * part2 of the register workflow sets the avatar
     */
    fun setAvatar(avatar: Avatar) {
        _avatar.value = avatar
    }

    /**
     * register the user if all information is given
     */
    fun register() {
        _userRegisteredState.postValue(Resource(ResourceState.LOADING, Unit, null))
        if (_userName.value.isNullOrBlank() || _password.value.isNullOrBlank() || _avatar.value == null)
            _userRegisteredState.postValue(
                Resource(
                    ResourceState.ERROR,
                    Unit,
                    R.string.register_error_create_user
                )
            )
        else {
            val params = CreateUserUseCase.Params(
                username = _userName.value!!,
                avatarName = _avatar.value!!.avatarName,
                password = _password.value!!
            )
            createUserUseCase.execute(params, object : DisposableCompletableObserver() {
                override fun onComplete() {
                    _userRegisteredState.postValue(Resource(ResourceState.SUCCESS, Unit, null))
                }

                override fun onError(e: Throwable) {
                    Timber.e(e.localizedMessage)
                    _userRegisteredState.postValue(
                        Resource(
                            ResourceState.ERROR, Unit,
                            when (e) {
                                is WeakPasswordException ->
                                    R.string.register_error_weak_password
                                is InvalidCredentialsException ->
                                    R.string.register_error_invalid_username
                                is UserCollisionException ->
                                    R.string.register_error_username_already_exists
                                else -> R.string.register_error_create_user
                            }
                        )
                    )
                }
            })
        }
    }

    override fun onCleared() {
        createUserUseCase.dispose()
        super.onCleared()
    }
}