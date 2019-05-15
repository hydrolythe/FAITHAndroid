package be.hogent.faith.faith.loginOrRegister.registerUserInfo

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent

class RegisterUserInfoViewModel : ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordRepeated = MutableLiveData<String>()

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _confirmUserInfoClicked = SingleLiveEvent<Unit>()
    val confirmUserInfoClicked: LiveData<Unit>
        get() = _confirmUserInfoClicked

    private val _userSavedSuccessFully = SingleLiveEvent<User>()
    val userSavedSuccessFully: LiveData<User>
        get() = _userSavedSuccessFully

    fun onConfirmUserInfoClicked() {
        if (password.value.isNullOrBlank()) {
            _errorMessage.postValue(R.string.register_password_empty)
            return
        }
        if (password.value != passwordRepeated.value) {
            _errorMessage.postValue(R.string.register_passwords_nomatch)
            return
        }
        _confirmUserInfoClicked.call()
    }
}