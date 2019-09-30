package be.hogent.faith.faith.loginOrRegister

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.repository.SignInException
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.LoginUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableMaybeObserver

class WelcomeViewModel(private val loginUserUseCase: LoginUserUseCase) : ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val _uuid = MutableLiveData<String>()
    val UUID: LiveData<String>
        get() = _uuid

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _registerButtonClicked = SingleLiveEvent<Unit>()
    val registerButtonClicked: LiveData<Unit>
        get() = _registerButtonClicked

    /*
    private val _loginButtonClicked = SingleLiveEvent<Unit>()
    val loginButtonClicked: LiveData<Unit>
        get() = _loginButtonClicked
*/


    private val _userLoggedInSuccessFully = SingleLiveEvent<Unit>()
    val UserLoggedInSuccessFully: LiveData<Unit>
        get() = _userLoggedInSuccessFully

    fun registerButtonClicked() {
        _registerButtonClicked.call()
    }

    fun loginButtonClicked() {
        login()
    }

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
        return true
    }

    fun login() {
        if (userNameIsValid() && passwordIsValid()) {
            val params = LoginUserUseCase.Params(userName.value!!, password.value!!)
            loginUserUseCase.execute(params, LoginUserUseCaseHandler())
        }
    }

    private inner class LoginUserUseCaseHandler : DisposableMaybeObserver<String?>() {
        override fun onSuccess(t: String) {
            Log.i(TAG, "success $t")
            _uuid.postValue(t)
            _userLoggedInSuccessFully.call()
        }

        override fun onComplete() {
            Log.i(TAG, "completed")
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, e.localizedMessage)
            _errorMessage.postValue(
                when (e) {
                    is SignInException -> R.string.login_error_wrong_username_or_password
                    else -> R.string.login_error
                }
            )
        }
    }
}