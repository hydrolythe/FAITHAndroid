package be.hogent.faith.faith.registerAvatar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class ResourceAvatarProvider(private val context: Context) : AvatarProvider {

    override fun getAvatarDrawables(): List<Drawable> {
        return getAvatars()
            .map(Avatar::avatarName)
            .map { avatarName -> getAvatarDrawable(avatarName) }
    }

    override fun getAvatarDrawable(avatarName: String): Drawable {
        val resourceId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)
        return ContextCompat.getDrawable(context, resourceId)!!
    }
}