package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.util.SingleLiveEvent

class RegisterUserInfoViewModel : ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRepeated = MutableLiveData<String>()
    val userNameErrorMessage = MutableLiveData<Int>()
    val passwordErrorMessage = MutableLiveData<Int>()
    val passwordRepeatErrorMessage = MutableLiveData<Int>()

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
            userNameErrorMessage.value = R.string.register_username_empty
            return false
        }
        return true
    }

    private fun passwordIsValid(): Boolean {
        if (password.value.isNullOrBlank()) {
            passwordErrorMessage.value = R.string.register_password_empty
            return false
        }
        if (password.value != passwordRepeated.value) {
            passwordRepeatErrorMessage.value = R.string.register_passwords_nomatch
            return false
        }
        return true
    }
}