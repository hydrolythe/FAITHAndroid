package be.hogent.faith.database.repositories

import androidx.lifecycle.MutableLiveData
import be.hogent.faith.database.R
import be.hogent.faith.domain.models.Avatar

class AvatarRepository {


    companion object {
        private val TAG = this::class.java.simpleName
    }


    private val avatarList = ArrayList<Avatar>()
    private val avatars : MutableLiveData<List<Avatar>> = MutableLiveData()

    init {

    }

    fun getAvatars(): MutableLiveData<List<Avatar>>{
        return avatars
    }

}