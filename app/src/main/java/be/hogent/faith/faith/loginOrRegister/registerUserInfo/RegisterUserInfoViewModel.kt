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

class RegisterUserInfoViewModel(private val isUsernameUniqueUseCase: IsUsernameUniqueUseCase) :
    ViewModel() {

    init {
        Log.i(TAG, "created")
    }

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRepeated = MutableLiveData<String>()

    private val _userNameErrorMessage = MutableLiveData<Int>()
    val userNameErrorMessage: LiveData<Int>
        get() = _userNameErrorMessage

    private val _passwordErrorMessage = MutableLiveData<Int>()
    val passwordErrorMessage: LiveData<Int>
        get() = _passwordErrorMessage

    private val _passwordRepeatErrorMessage = MutableLiveData<Int>()
    val passwordRepeatErrorMessage: LiveData<Int>
        get() = _passwordRepeatErrorMessage

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    fun onConfirmUserInfoClicked() {
        confirmUserInfo()
    }

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

    private fun confirmUserInfo() {
        if (userNameIsValid() && passwordIsValid()) {
            val params = IsUsernameUniqueUseCase.Params(userName.value!!)
            isUsernameUniqueUseCase.execute(params, IsUsernameUniqueUseCaseHandler())
        }
    }

    private inner class IsUsernameUniqueUseCaseHandler : DisposableSingleObserver<Boolean>() {

        override fun onSuccess(t: Boolean) {
            if (t)
                _errorMessage.postValue(R.string.register_error_username_already_exists)
            else
                _userInfoConfirmedSuccesFully.call()
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