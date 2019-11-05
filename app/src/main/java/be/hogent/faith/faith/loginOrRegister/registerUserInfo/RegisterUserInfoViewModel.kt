package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.util.SingleLiveEvent

class RegisterUserInfoViewModel : ViewModel() {

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

    private val _confirmUserInfoClicked = SingleLiveEvent<Unit>()
    val confirmUserInfoClicked: LiveData<Unit>
        get() = _confirmUserInfoClicked

    fun onConfirmUserInfoClicked() {
        if (userNameIsValid() && passwordIsValid()) {
            _confirmUserInfoClicked.call()
        }
    }



    private fun userNameIsValid(): Boolean {
        if (userName.value.isNullOrBlank()) {
            _userNameErrorMessage.value = R.string.register_username_empty
            return false
        }
        _userNameErrorMessage.value = R.string.empty
        return true
    }

    private fun passwordIsValid(): Boolean {
        if (password.value.isNullOrBlank()) {
            _passwordErrorMessage.value = R.string.register_password_empty
            return false
        }
        if (password.value != passwordRepeated.value) {
            _passwordRepeatErrorMessage.value = R.string.register_passwords_nomatch
            return false
        }
        return true
    }
}