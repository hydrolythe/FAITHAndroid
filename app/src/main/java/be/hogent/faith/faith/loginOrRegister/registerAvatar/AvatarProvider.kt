package be.hogent.faith.faith.loginOrRegister.registerAvatar

import android.graphics.drawable.Drawable

interface AvatarProvider {
    /**
     * Provides a list of all the names of the avatars.
     * This can be seen as a repository of all the currently available avatars.
     */
    fun getAvatars(skinColor: SkinColor): List<Avatar> {
        return listOf(
            "jongen_gamer",
            "meisje_stoer",
            "jongen_newwave",
            "meisje_newwave",
            "jongen_alternatief",
            "meisje_creatief",
            "jongen_boysband",
            "meisje_girly",
            "jongen_klassiek",
            "meisje_klassiek",
            "jongen_modieus",
            "meisje_modieus",
            "jongen_voetballer",
            "meisje_sportief"
        ).map { name -> Avatar("${name}_${skinColor}") }
    }

    /**
     * Returns a list of drawables, one for each entry in [getAvatars]
     */
    fun getAvatarDrawables(skinColor: SkinColor): List<Drawable>

    /**
     * Get the drawable for a specific entry in [getAvatars]
     */
    fun getAvatarDrawable(avatarName: String): Drawable

    fun getAvatarDrawableStaan(avatarName: String): Drawable
    fun getAvatarDrawableZitten(avatarName: String): Drawable
    fun getAvatarDrawableGezicht(avatarName: String): Drawable
    fun getAvatarDrawableOutline(avatarName: String): Drawable
    fun getAvatarDrawableOutlineId(avatarName: String): Int
}

enum class SkinColor { bl, lb, db }