package be.hogent.faith.faith.registerAvatar

import android.graphics.drawable.Drawable

interface AvatarProvider {
    /**
     * Provides a list of all the names of the avatars.
     * This can be seen as a repository of all the currently available avatars.
     */
    fun getAvatars(): List<Avatar> {
        return listOf(
            "jongen_gamer",
            "meisje_stoer",
            "avatar",
            "avatar2",
            "avatar3",
            "avatar4",
            "avatar5"
        ).map { name -> Avatar(name) }
    }

    /**
     * Returns a list of drawables, one for each entry in [avatarNames]
     */
    fun getAvatarDrawables(): List<Drawable>

    /**
     * Get the drawable for a specific entry in [avatarNames]
     */
    fun getAvatarDrawable(avatarName: String): Drawable
}