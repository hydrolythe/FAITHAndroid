package be.hogent.faith.faith.chooseAvatar.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.Avatar
import java.security.AccessController

class ListViewModel : ViewModel() {


    var avatars = MutableLiveData<List<Avatar>>()

    init {
        fetchAvatars()
    }

    /**
     * Fetches the avatars.
     */
    fun fetchAvatars() {
        val avatar = Avatar("https://avatars.dicebear.com/v2/male/harm.svg")
        var avList = ArrayList<Avatar>()
        avList.add(avatar)
        avatars.value = avList
    }

}