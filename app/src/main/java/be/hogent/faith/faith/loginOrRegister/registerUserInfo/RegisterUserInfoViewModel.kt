package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.repository.InvalidCredentialsException
import be.hogent.faith.domain.repository.UserCollisionException
import be.hogent.faith.domain.repository.WeakPasswordException
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.RegisterUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableMaybeObserver

class RegisterUserInfoViewModel(private val registerUserUseCase: RegisterUserUseCase) :
    ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRepeated = MutableLiveData<String>()
    private val _uuid = MutableLiveData<String>()
    val UUID: LiveData<String>
        get() = _uuid

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _confirmUserInfoClicked = SingleLiveEvent<Unit>()
    val confirmUserInfoClicked: LiveData<Unit>
        get() = _confirmUserInfoClicked

    fun onConfirmUserInfoClicked() {
        register()
    }

    private val _userRegisteredSuccessFully = SingleLiveEvent<Unit>()
    val UserRegisteredSuccessFully: LiveData<Unit>
        get() = _userRegisteredSuccessFully


    private fun userNameIsValid(): Boolean {
        if (userName.value.isNullOrBlank()) {
            _errorMessage.postValue(R.string.registerOrLogin_username_empty)
            return false
        }
        return true
    }

    private fun passwordIsValid(): Boolean {
        if (password.value.isNullOrBlank()) {
            _errorMessage.postValue(R.string.registerOrLogin_password_empty)
            return false
        }
        if (password.value != passwordRepeated.value) {
            _errorMessage.postValue(R.string.register_passwords_nomatch)
            return false
        }
        return true
    }

    fun register() {
        if (userNameIsValid() && passwordIsValid()) {
            val params = RegisterUserUseCase.Params(userName.value!!, password.value!!)
            registerUserUseCase.execute(params, RegisterUserUseCaseHandler())
        }
    }

    private inner class RegisterUserUseCaseHandler : DisposableMaybeObserver<String?>() {
        override fun onSuccess(t: String) {
            Log.i(TAG, "success $t")
            _uuid.postValue(t)
            _userRegisteredSuccessFully.call()
        }

        override fun onComplete() {
            Log.i(TAG, "completed")
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, e.localizedMessage)
            _errorMessage.postValue(
                when (e) {
                    is WeakPasswordException -> R.string.register_error_weak_password
                    is InvalidCredentialsException -> R.string.register_error_invalid_username
                    is UserCollisionException -> R.string.register_error_username_already_exists
                    else -> R.string.register_error_create_user
                }
            )
        }
    }
}