package be.hogent.faith.faith.util

import android.content.Context
import android.util.Log
import be.hogent.faith.faith.chooseAvatar.fragments.Avatar

class AvatarProvider(private val context: Context) {

    fun getAvatars(): List<Avatar> {
        val avatar1 = getAvatarEntity("avatar")
        val avatar2 = getAvatarEntity("avatar2")
        val avatar3 = getAvatarEntity("avatar3")
        val avatar4 = getAvatarEntity("avatar4")
        val avatar5 = getAvatarEntity("avatar5")
        return listOf(avatar1, avatar2, avatar3, avatar4, avatar5)
    }

    private fun getAvatarEntity(resourceName: String): Avatar {
        val resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName())
        Log.d(TAG, "Avatar $resourceId")
        val avatar = Avatar(
            resourceId,
            resourceName
        )
        Log.d(TAG, "Avatar ${avatar.imageName}")
        return avatar
    }
}