package be.hogent.faith.faith.chooseAvatar.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.domain.models.AvatarItem

/**
 * Abstract ViewModel class to represent the [AvatarItem] elements.
 */
class AvatarItemViewModel : ViewModel() {

    /**
     * Private mutable live data object which keeps track of the AvatarItems.
     */
    private var _avatarItems = MutableLiveData<List<AvatarItem>>()


    private var _backpackItems = MutableLiveData<List<AvatarItem>>()

    /**
     * Getter for the avatarItems, but only returns a [LiveData<AvatarItem>] type.
     */
    val avatarItems: LiveData<List<AvatarItem>>
    get() = _avatarItems

    /**
     * Getter for the backpack items
     */
    val backpackItems : LiveData<List<AvatarItem>>
    get() = _backpackItems

    init {
        fetchItems()
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

        val backpack1 = Avatar(R.drawable.avatar)
        val backpack2 = Avatar(R.drawable.avatar2)
        val backpack3 = Avatar(R.drawable.avatar3)
        val backpack4 = Avatar(R.drawable.avatar4)
        val backpack5 = Avatar(R.drawable.avatar5)
        val bpList = ArrayList<AvatarItem>()
        bpList.add(backpack1)
        bpList.add(backpack2)
        bpList.add(backpack3)
        bpList.add(backpack4)
        bpList.add(backpack5)
        _backpackItems.postValue(bpList)
    }


}