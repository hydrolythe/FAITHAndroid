package be.hogent.faith.faith.loginOrRegister.registerAvatar

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent

/**
 * ViewModel for the Avatar selection screen
 */
class RegisterAvatarViewModel(
    private val avatarProvider: AvatarProvider
) : ViewModel() {

    private var _avatars = MutableLiveData<List<Avatar>>()
    val avatars: LiveData<List<Avatar>>
        get() = _avatars

    val selectedAvatarPosition: LiveData<Long>
        get() = _selectedAvatarPosition
    private var _selectedAvatarPosition = MutableLiveData<Long>()

    private val _finishRegistrationClicked = SingleLiveEvent<User>()
    val finishRegistrationClicked: LiveData<User>
        get() = _finishRegistrationClicked

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    init {
        fetchAvatarImages()
        // Set initially to -1 = no selection has been provided.
        _selectedAvatarPosition.postValue(-1)
    }

    /**
     * Will always hold the [Avatar] corresponding with the [_selectedAvatarPosition].
     * Exposing a LiveData would be nicer (using a map on [_selectedAvatarPosition] but that only updates
     * when someone is actually observing this Livedata. As we only need it once no observing is needed.
     */
    val selectedAvatar: Avatar?
        get() {
            return if (avatarWasSelected()) {
                _avatars.value!![selectedAvatarPosition.value!!.toInt()]
            } else {
                null
            }
        }

    /**
     * Returns true if an Avatar has been selected, false if not.
     */
    fun avatarWasSelected(): Boolean {
        with(selectedAvatarPosition.value) {
            return this != null && this.toInt() != -1
        }
    }

    fun onFinishRegistrationClicked() {
        if (avatarWasSelected()) {
            _finishRegistrationClicked.call()
        } else {
            _errorMessage.postValue(R.string.register_avatar_mustSelect)
        }
    }

    /**
     * Sets the selected item (the selected avatars). In this case this is
     * still the position in the recyclerView.
     */
    fun setSelectedAvatar(position: Long) {
        _selectedAvatarPosition.postValue(position)
    }

    private fun fetchAvatarImages() {
        _avatars.value = (avatarProvider.getAvatars())
    }
}