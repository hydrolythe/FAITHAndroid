package be.hogent.faith.faith.chooseAvatar.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.User
import be.hogent.faith.util.TAG

/**
 * Represents the [ViewModel] for the [User] throughout the the application.
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

    init {
        Log.d(TAG, "new userViewModel created")
    }
}