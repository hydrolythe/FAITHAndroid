package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.repository.NetworkError
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.service.usecases.IsUsernameUniqueUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

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

    fun onConfirmUserInfoClicked() {
        confirmUserInfo()
    }

    /**
     * when username and password is entered and username is unique
     */
    private val _userConfirmedState = MutableLiveData<Resource<Unit>>()
    val userConfirmedState: LiveData<Resource<Unit>>
        get() = _userConfirmedState

    private fun userNameIsValid(): Boolean {
        if (userName.value.isNullOrBlank()) {
            _userNameErrorMessage.value = R.string.registerOrLogin_username_empty
            return false
        }
        _userNameErrorMessage.value = R.string.empty
        return true
    }

    private fun passwordIsValid(): Boolean {
        _passwordErrorMessage.value = R.string.empty
        _passwordRepeatErrorMessage.value = R.string.empty
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
            _userConfirmedState.postValue(Resource(ResourceState.LOADING, Unit, null))
            val params = IsUsernameUniqueUseCase.Params(userName.value!!)
            isUsernameUniqueUseCase.execute(params, IsUsernameUniqueUseCaseHandler())
        }
    }

    private inner class IsUsernameUniqueUseCaseHandler : DisposableSingleObserver<Boolean>() {

        override fun onSuccess(t: Boolean) {
            if (t)
                _userConfirmedState.postValue(Resource(ResourceState.SUCCESS, Unit, null))
            else
                _userConfirmedState.postValue(
                    Resource(
                        ResourceState.ERROR,
                        Unit,
                        R.string.register_error_username_already_exists
                    )
                )
        }

        override fun onError(e: Throwable) {
            Timber.e("$TAG: e.localizedMessage")
            _userConfirmedState.postValue(
                Resource(
                    ResourceState.ERROR, Unit,
                    when (e) {
                        is NetworkError -> R.string.login_error_internet
                        else -> R.string.register_error_create_user
                    }
                )
            )
        }
    }

    override fun onCleared() {
        isUsernameUniqueUseCase.dispose()
        super.onCleared()
    }
}