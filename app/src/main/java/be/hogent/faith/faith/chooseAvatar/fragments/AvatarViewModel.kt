package be.hogent.faith.faith.chooseAvatar.fragments

import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.domain.models.AvatarItem

/**
 * ViewModel used to represent the list of Avatars.
 */
class AvatarViewModel : AvatarItemViewModel() {

    /**
     * Fetches the Avatars.
     */
    override fun fetchItems() {
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