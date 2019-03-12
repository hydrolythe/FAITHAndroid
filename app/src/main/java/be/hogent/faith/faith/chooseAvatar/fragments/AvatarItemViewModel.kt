package be.hogent.faith.faith.chooseAvatar.fragments

import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.domain.models.AvatarItem
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.ui.MainActivity
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.faith.util.TAG

/**
 * Abstract ViewModel class to represent the [AvatarItem] elements.
 */
class AvatarItemViewModel : ViewModel() {

    /**
     * Private mutable live data object which keeps track of the AvatarItems.
     */
    private var _avatarItems = MutableLiveData<List<AvatarItem>>()

    private var _selectedItem = MutableLiveData<Long>()

    private val _nextButtonClicked = SingleLiveEvent<Unit>()


    val nextButtonClicked: LiveData<Unit>
        get() = _nextButtonClicked

    /**
     * User name of the user
     */
    var userName = MutableLiveData<String>()

    init {
        //Set initially to -1 = no selection has been provided.
        _selectedItem.value = -1
    }

    /**
     * Getter for the avatarItems, but only returns a [LiveData<AvatarItem>] type.
     */
    val avatarItems: LiveData<List<AvatarItem>>
        get() = _avatarItems

    /**
     * The item selected in the recyclerview as the avatar.
     */
    val selectedItem: LiveData<Long>
        get() = _selectedItem


    fun isSelected(): Boolean {
        if (_selectedItem.value!!.toInt() == -1) {
            return false
        }
        return true
    }

    /**
     * Sets the selected item (the selected avatars). In this case this is
     * still the position in the rv.
     */
    fun setSelectedItem(selectedItem: Long) {
        _selectedItem.postValue(selectedItem)
    }


    init {
        fetchItems()
    }

    fun nextButtonPressed() {


        if (_selectedItem.value!!.toInt() >= 0) {
            var user = User(
                avatar = _avatarItems.value?.get(_selectedItem.value!!.toInt()) as Avatar,
                username = "test"
            )
            Log.i(TAG, "Found ${userName.value}")
            Log.i(TAG,"Found item : ${selectedItem.value}")
        } else {
            //TODO: update the user that he has not yet selected an avatar
        }
        Log.i(TAG, "Pressed the button")
        _nextButtonClicked.call()
    }


    private fun fetchItems() {
        val avatar1 = Avatar(R.drawable.avatar)
        val avatar2 = Avatar(R.drawable.avatar2)
        val avatar3 = Avatar(R.drawable.avatar3)
        val avatar4 = Avatar(R.drawable.avatar4)
        val avatar5 = Avatar(R.drawable.avatar5)
        val avList = ArrayList<AvatarItem>()
        avList.add(avatar1)
        avList.add(avatar2)
        avList.add(avatar3)
        avList.add(avatar4)
        avList.add(avatar5)
        _avatarItems.postValue(avList)
    }
}