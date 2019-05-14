package be.hogent.faith.faith.loginOrRegister

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class WelcomeViewModel : ViewModel() {

    private val _registerButtonClicked = SingleLiveEvent<Unit>()
    val registerButtonClicked: LiveData<Unit>
        get() = _registerButtonClicked

    fun registerButtonClicked() {
        _registerButtonClicked.call()
    }
}