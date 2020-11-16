package be.hogent.faith.faith.util

import android.content.Context

private const val DB_NAME = "faithDB"
private const val KEY_AVATAR_NAME = "KEY_AVATAR_NAME"

class SharedPreferencesHelper {
    companion object {

        fun getAvatarName(context: Context): String {
            return context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE)
                .getString(KEY_AVATAR_NAME, "")
        }

        fun setAvatarName(context: Context, name: String?) {
            context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_AVATAR_NAME, name).apply()
        }
    }
}