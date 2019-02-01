package be.hogent.faith.faith.chooseAvatar.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar

class ListViewModel : ViewModel() {


    var avatars = MutableLiveData<List<Avatar>>()

    init {
        fetchAvatars()
    }

    /**
     * Fetches the avatars.
     */
    fun fetchAvatars() {
        val avatar1 = Avatar(R.drawable.avatar)
        val avatar2 = Avatar(R.drawable.avatar2)
        val avatar3 = Avatar(R.drawable.avatar3)
        val avatar4 = Avatar(R.drawable.avatar4)
        val avatar5 = Avatar(R.drawable.avatar5)


        val avList = ArrayList<Avatar>()
        avList.add(avatar1)
        avList.add(avatar2)
        avList.add(avatar3)
        avList.add(avatar4)
        avList.add(avatar5)
        avatars.value = avList
    }

}