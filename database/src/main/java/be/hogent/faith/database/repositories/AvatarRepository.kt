package be.hogent.faith.database.repositories

import androidx.lifecycle.MutableLiveData
import be.hogent.faith.domain.models.AvatarItem

class AvatarRepository {

    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val avatarList = ArrayList<AvatarItem>()
    private val avatars: MutableLiveData<List<AvatarItem>> = MutableLiveData()

    init {
    }

    fun getAvatars(): MutableLiveData<List<AvatarItem>> {
        return avatars
    }
}