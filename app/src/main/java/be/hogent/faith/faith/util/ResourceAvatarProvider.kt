package be.hogent.faith.faith.util

import android.content.Context
import android.graphics.drawable.Drawable
import be.hogent.faith.faith.registerAvatar.Avatar
import be.hogent.faith.faith.registerAvatar.AvatarProvider

class ResourceAvatarProvider(private val context: Context) : AvatarProvider {

    override fun getAvatarDrawables(): List<Drawable> {
        return getAvatars()
            .map(Avatar::avatarName)
            .map { avatarName -> getAvatarDrawable(avatarName) }
    }

    override fun getAvatarDrawable(avatarName: String): Drawable {
        val resourceId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)
        return context.resources.getDrawable(resourceId)
//        return ContextCompat.getDrawable(context, resourceId)!!
    }
}