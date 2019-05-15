package be.hogent.faith.faith.loginOrRegister.registerAvatar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private var _selectedItem = MutableLiveData<Long>()

    private val _nextButtonClicked = SingleLiveEvent<Unit>()
    val nextButtonClicked: LiveData<Unit>
        get() = _nextButtonClicked

    /**
     * Updates with the new User when it was saved successfully.
     */
    private val _userSavedSuccessFully = SingleLiveEvent<User>()
    val userSavedSuccessFully: LiveData<User>
        get() = _userSavedSuccessFully

    private val _inputErrorMessageID = MutableLiveData<Int>()
    val inputErrorMessageID: LiveData<Int>
        get() = _inputErrorMessageID

    /**
     * Will be updated with the latest error message when an error occurs when saving
     */
    private val _userSaveFailed = MutableLiveData<String>()
    val userSaveFailed: LiveData<String>
        get() = _userSaveFailed

    init {
        // Set initially to -1 = no selection has been provided.
        _selectedItem.value = -1

        fetchAvatarImages()
    }

    /**
     * The item selected in the recyclerView as the avatarName.
     */
    val selectedItem: LiveData<Long>
        get() = _selectedItem

    /**
     * Returns true if an Avatar has been selected, false if not.
     */
    fun avatarWasSelected(): Boolean {
        return _selectedItem.value!!.toInt() != -1
    }

    /**
     * Sets the selected item (the selected avatars). In this case this is
     * still the position in the recyclerView.
     */
    fun setSelectedItem(selectedItem: Long) {
        _selectedItem.postValue(selectedItem)
    }

    private fun fetchAvatarImages() {
        _avatars.postValue(avatarProvider.getAvatars())
    }
}