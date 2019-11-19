package be.hogent.faith.storage

import android.content.Context
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File

class StoragePathProvider(private val context: Context,
    private val fbAuth: FirebaseAuth
) {

    private val user: FirebaseUser?
        get() = fbAuth.currentUser

    fun getEventFolder(event: Event): File {
        return File("users/${user!!.uid}/events/${event.uuid}")
    }

    fun getDetailPath(event: Event, detail: Detail): File {
        return File("${getEventFolder(event).path}/${detail.uuid}")
    }

    fun getEmotionAvatarPath(event: Event): File {
        return File("${getEventFolder(event).path}/avatar")
    }

    fun getLocalEmotionAvatarPath(event:Event) : File{
        return  File(context.filesDir, getEmotionAvatarPath(event).path)
    }

    fun getLocalDetailPath(detail:Detail) : File{
        return  File(context.filesDir, detail.file.path)
    }
}