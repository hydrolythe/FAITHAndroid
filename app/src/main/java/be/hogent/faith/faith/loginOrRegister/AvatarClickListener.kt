package be.hogent.faith.faith.loginOrRegister

import android.view.View

interface AvatarClickListener{


    /**
     * Method which is called when an avatar is selected.
     */
    fun onAvatarClicked(v : View, position: Int);
}