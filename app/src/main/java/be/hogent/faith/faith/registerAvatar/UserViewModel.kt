package be.hogent.faith.faith.registerAvatar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.User

/**
 * Represents the [ViewModel] for the [User] throughout the the application.
 * It should be injected only using the scope specified in the KoinModules file.
 */
class UserViewModel : ViewModel() {

    private var _user = MutableLiveData<User>()

    /**
     * Returns a LiveData object for the [User].
     */
    val user: LiveData<User>
        get() = _user

    /**
     * Sets the [User] for this ViewModel.
     */
    fun setUser(user: User) {
        _user.postValue(user)
    }
}