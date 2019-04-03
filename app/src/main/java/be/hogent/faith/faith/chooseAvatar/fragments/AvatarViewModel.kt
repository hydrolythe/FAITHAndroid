package be.hogent.faith.faith.chooseAvatar.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.util.TAG

/**
 * ViewModel for the Avatar selection screen
 */
class AvatarViewModel : ViewModel() {

    /**
     * Private mutable live data object which keeps track of the AvatarItems.
     */
    private var _avatarItems = MutableLiveData<List<Avatar>>()

    private var _selectedItem = MutableLiveData<Long>()

    private val _nextButtonClicked = SingleLiveEvent<Unit>()
    val nextButtonClicked: LiveData<Unit>
        get() = _nextButtonClicked

    /**
     * User recordingName of the user
     */
    var userName = MutableLiveData<String>()

    init {
        // Set initially to -1 = no selection has been provided.
        _selectedItem.value = -1

        fetchItems()
    }

    /**
     * Getter for the avatarItems, but only returns a [LiveData<AvatarItem>] type.
     */
    val avatarItems: LiveData<List<Avatar>>
        get() = _avatarItems

    /**
     * The item selected in the recyclerView as the avatar.
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

    fun nextButtonPressed() {
        if (avatarWasSelected() && userName.value != null) {
            var user = User(
                avatar = _avatarItems.value?.get(_selectedItem.value!!.toInt()) as Avatar,
                username = userName.value!!
            )
            Log.i(TAG, "Found ${userName.value}")
            Log.i(TAG, "Found item : ${selectedItem.value}")
        } else {
            // TODO: update the user that he has not yet selected an avatar
        }
        Log.i(TAG, "Pressed the button")
        _nextButtonClicked.call()
    }

    /**
     * TODO: Needs to be adapted to the way Avatars will be provided (Network, DB, ...)
     */
    private fun fetchItems() {
        val avatar1 = Avatar(R.drawable.avatar)
        val avatar2 = Avatar(R.drawable.avatar2)
        val avatar3 = Avatar(R.drawable.avatar3)
        val avatar4 = Avatar(R.drawable.avatar4)
        val avatar5 = Avatar(R.drawable.avatar5)
        val avList = listOf(avatar1, avatar2, avatar3, avatar4, avatar5)
        _avatarItems.postValue(avList)
    }
}