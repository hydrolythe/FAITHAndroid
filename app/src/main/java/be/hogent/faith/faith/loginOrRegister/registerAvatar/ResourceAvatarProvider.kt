package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import timber.log.Timber

class ResourceAvatarProvider(private val context: Context) :
    AvatarProvider {

    override fun getAvatarDrawables(): List<Drawable> {
        return getAvatars()
            .map(Avatar::avatarName)
            .map { avatarName -> getAvatarDrawable(avatarName) }
    }

    override fun getAvatarDrawable(avatarName: String): Drawable {
        return getDrawable(avatarName)
    }

    override fun getAvatarDrawableStaan(avatarName: String): Drawable {
        return getDrawable(avatarName, "staan")
    }

    override fun getAvatarDrawableZitten(avatarName: String): Drawable {
        return getDrawable(avatarName, "zitten")
    }

    override fun getAvatarDrawableGezicht(avatarName: String): Drawable {
        return getDrawable(avatarName, "gezicht")
    }

    override fun getAvatarDrawableOutline(avatarName: String): Drawable {
        return getDrawable(avatarName, "outline")
    }

    private fun getDrawable(avatarName: String, type: String? = null): Drawable {
        val avatarResourceName = if (type != null) "${avatarName}_$type" else avatarName
        Timber.d("Resource :$avatarResourceName")
        val resourceId =
            context.resources.getIdentifier(avatarResourceName, "drawable", context.packageName)
        Timber.d("Resource :$resourceId")
        return ContextCompat.getDrawable(context, resourceId)!!
    }

    override fun getAvatarDrawableOutlineId(avatarName: String): Int {
        return context.getResources().getIdentifier(
            "${avatarName}_outline", "drawable",
            context.getPackageName()
        )
    }
}