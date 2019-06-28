package be.hogent.faith.faith.loginOrRegister

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class WelcomeViewModel : ViewModel() {

    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _registerButtonClicked = SingleLiveEvent<Unit>()
    val registerButtonClicked: LiveData<Unit>
        get() = _registerButtonClicked

    private val _loginButtonClicked = SingleLiveEvent<Unit>()
    val loginButtonClicked: LiveData<Unit>
        get() = _loginButtonClicked

    fun registerButtonClicked() {
        _registerButtonClicked.call()
    }

    fun loginButtonClicked() {
        _loginButtonClicked.call()
    }

    fun gotoCityButtonClicked() {
        userName.value = "Jan"
        _loginButtonClicked.call()
    }
}