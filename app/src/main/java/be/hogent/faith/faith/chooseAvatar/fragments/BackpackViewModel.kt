package be.hogent.faith.faith.chooseAvatar.fragments

import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.domain.models.AvatarItem

/**
 * ViewModel used to list the backpacks.
 */
class BackpackViewModel : AvatarItemViewModel() {


    /**
     * Fetches the backpacks.
     */
    override fun fetchItems() {
        val backpack1 = Avatar(R.drawable.avatar)
        val backpack2 = Avatar(R.drawable.avatar2)
        val backpack3 = Avatar(R.drawable.avatar3)
        val backpack4 = Avatar(R.drawable.avatar4)
        val backpack5 = Avatar(R.drawable.avatar5)
        val avList = ArrayList<AvatarItem>()
        avList.add(backpack1)
        avList.add(backpack2)
        avList.add(backpack3)
        avList.add(backpack4)
        avList.add(backpack5)
        _avatarItems.postValue(avList)
    }
}