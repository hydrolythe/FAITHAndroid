package be.hogent.faith.faith.loginOrRegister

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.repository.NetworkError
import be.hogent.faith.domain.repository.SignInException
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.LoginUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableMaybeObserver
import timber.log.Timber

class WelcomeViewModel(private val loginUserUseCase: LoginUserUseCase) : ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _uuid = MutableLiveData<String>()
    val UUID: LiveData<String>
        get() = _uuid

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    /**
     * reports errors with username
     */
    private val _userNameErrorMessage = MutableLiveData<Int>()
    val userNameErrorMessage: LiveData<Int>
        get() = _userNameErrorMessage

    /**
     * reports errors with password
     */
    private val _passwordErrorMessage = MutableLiveData<Int>()
    val passwordErrorMessage: LiveData<Int>
        get() = _passwordErrorMessage


    private val _registerButtonClicked = SingleLiveEvent<Unit>()
    val registerButtonClicked: LiveData<Unit>
        get() = _registerButtonClicked

    private val _userLoggedInSuccessFully = SingleLiveEvent<Unit>()
    val userLoggedInSuccessFully: LiveData<Unit>
        get() = _userLoggedInSuccessFully

    init {
        // TODO Moet er gecontrolleerd worden of de user reeds aangemeld is: eerst nagaan of user reeds is aangemeld (isUserLoggedInUseCase), dan user ophalen en dan call van userLoggedInSuccessfully
    }

    /**
     * user is new and wants to register
     */
    fun registerButtonClicked() {
        _registerButtonClicked.call()
    }

    /**
     * logs in a user
     */
    fun loginButtonClicked() {
        login()
    }

    private fun userNameIsValid(): Boolean {
        if (userName.value.isNullOrBlank()) {
            userName.value=""
            _userNameErrorMessage.value = R.string.registerOrLogin_username_empty
            return false
        }
        _userNameErrorMessage.value = R.string.empty
        return true
    }

    private fun passwordIsValid(): Boolean {
        val pwd = password.value
        if (pwd.isNullOrBlank() || pwd.length < 6) {
            this.password.value = ""
            _passwordErrorMessage.value = R.string.registerOrLogin_password_empty
            return false
        }
        _passwordErrorMessage.value = R.string.empty
        return true
    }

    /**
     * validates username and password. Logs in the user
     */
    private fun login() {
        if (userNameIsValid() && passwordIsValid()) {
            val params = LoginUserUseCase.Params(userName.value!!, password.value!!)
            loginUserUseCase.execute(params, LoginUserUseCaseHandler())
        }
    }

    private inner class LoginUserUseCaseHandler : DisposableMaybeObserver<String?>() {
        // returns uuid when successfully logged in
        override fun onSuccess(t: String) {
            _uuid.postValue(t)
            _userLoggedInSuccessFully.call()
        }

        override fun onComplete() {
        }

        override fun onError(e: Throwable) {
            Timber.e("${TAG}: e.localizedMessage")
            _errorMessage.postValue(
                when (e) {
                    is NetworkError -> R.string.login_error_internet
                    is SignInException -> R.string.login_error_wrong_username_or_password
                    else -> R.string.login_error
                }
            )
        }
    }

    override fun onCleared() {
        loginUserUseCase.dispose()
        super.onCleared()
    }
}