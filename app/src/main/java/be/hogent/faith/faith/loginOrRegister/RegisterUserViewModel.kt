package be.hogent.faith.faith.loginOrRegister

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.repository.InvalidCredentialsException
import be.hogent.faith.domain.repository.UserCollisionException
import be.hogent.faith.domain.repository.WeakPasswordException
import be.hogent.faith.faith.loginOrRegister.registerAvatar.Avatar
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.RegisterUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableCompletableObserver

/**
 * Represents the [ViewModel] for the user during teh registering process.
 * It is shared by the fragments needed for the workflow of registering
 * If all the information is available, the user will be registered
 */
class RegisterUserViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    private val _password = MutableLiveData<String>()
    private val _avatar = MutableLiveData<Avatar>()

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _userRegisteredSuccessFully = SingleLiveEvent<Unit>()
    val userRegisteredSuccessFully: LiveData<Unit>
        get() = _userRegisteredSuccessFully

    fun setRegistrationDetails(userName: String, password: String) {
        _userName.value = userName
        _password.value = password
    }

    fun setAvatar(avatar: Avatar) {
        _avatar.value = avatar
    }

    fun register() {
        if (_userName.value.isNullOrBlank() || _password.value.isNullOrBlank() || _avatar.value == null)
            _errorMessage.postValue(R.string.register_error_create_user)

        val params = RegisterUserUseCase.Params(
            _userName.value!!,
            _password.value!!,
            _avatar.value!!.avatarName
        )
        registerUserUseCase.execute(params, RegisterUserUseCaseHandler())
    }

    private inner class RegisterUserUseCaseHandler : DisposableCompletableObserver() {
        override fun onComplete() {
            _userRegisteredSuccessFully.call()
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, e.localizedMessage)
            _errorMessage.postValue(
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
        }
    }

    override fun onCleared() {
        registerUserUseCase.dispose()
        super.onCleared()
    }
}