package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.repository.NetworkError
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.IsUsernameUniqueUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableSingleObserver

/**
 * Represents the [ViewModel] for the user during the registering process - Part 1
 * Namely the part where the user enters username and password
 * It validates the userinfo and checks if username is unique
 * If all the information is available, the user will be registered
 */
class RegisterUserInfoViewModel(private val isUsernameUniqueUseCase: IsUsernameUniqueUseCase) :
    ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRepeated = MutableLiveData<String>()

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

    /**
     * reports errors with passwordrepeat
     */
    private val _passwordRepeatErrorMessage = MutableLiveData<Int>()
    val passwordRepeatErrorMessage: LiveData<Int>
        get() = _passwordRepeatErrorMessage

    /**
     * reports errors with calling use case IsUniqueEmail
     */
    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun onConfirmUserInfoClicked() {
        confirmUserInfo()
    }

    /**
     * when username and password is entered and username is unique
     */
    private val _userInfoConfirmedSuccesFully = SingleLiveEvent<Unit>()
    val userInfoConfirmedSuccessfully: LiveData<Unit>
        get() = _userInfoConfirmedSuccesFully

    private fun userNameIsValid(): Boolean {
        if (userName.value.isNullOrBlank()) {
            _userNameErrorMessage.value = R.string.registerOrLogin_username_empty
            return false
        }
        return true
    }

    private fun passwordIsValid(): Boolean {
        val password = password.value
        if (password.isNullOrBlank() || password.length < 6) {
            this.password.value = "" // anders wordt de fout niet getoond
            this.passwordRepeated.value = ""
            _passwordErrorMessage.value = R.string.registerOrLogin_password_empty
            return false
        }

        if (password != passwordRepeated.value) {
            this.passwordRepeated.value = "" // anders wordt de fout niet getoond
            _passwordRepeatErrorMessage.value = R.string.register_passwords_nomatch
            return false
        }
        return true
    }

    /**
     * user entered username, password and passwordrepeat.
     * Validates input and checks if username is unique
     */
    private fun confirmUserInfo() {
        if (userNameIsValid() && passwordIsValid()) {
            val params = IsUsernameUniqueUseCase.Params(userName.value!!)
            isUsernameUniqueUseCase.execute(params, IsUsernameUniqueUseCaseHandler())
        }
    }

    private inner class IsUsernameUniqueUseCaseHandler : DisposableSingleObserver<Boolean>() {

        override fun onSuccess(t: Boolean) {
            if (t)
                _userInfoConfirmedSuccesFully.call()
            else
                _errorMessage.postValue(R.string.register_error_username_already_exists)
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, e.localizedMessage)
            _errorMessage.postValue(
                when (e) {
                    is NetworkError -> R.string.login_error_internet
                    else -> R.string.register_error_create_user
                }
            )
        }
    }

    override fun onCleared() {
        isUsernameUniqueUseCase.dispose()
        super.onCleared()
    }
}