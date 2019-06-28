package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import org.koin.android.viewmodel.ext.android.viewModel

class ResourceAvatarProvider(private val context: Context) :
    AvatarProvider {


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