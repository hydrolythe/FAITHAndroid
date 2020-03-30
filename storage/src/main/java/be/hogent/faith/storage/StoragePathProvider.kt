package be.hogent.faith.storage

import android.content.Context
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File

class StoragePathProvider(
    private val context: Context,
    private val fbAuth: FirebaseAuth
) {

    private val user: FirebaseUser?
        get() = fbAuth.currentUser

    /**
     * Returns the folder path in which a user's detailsContainer will be saved.
     */
    fun getDetailsContainerFolder(detailsContainer: DetailsContainer): File {
        return when (detailsContainer) {
            is Event -> File("users/${user!!.uid}/events/${detailsContainer.uuid}")
            is Backpack -> File("users/${user!!.uid}/backpack")
            else -> throw NotImplementedError()
        }
    }

    /**
     * Returns the path in which a detail will be saved
     */
    fun getDetailPath(detailsContainer: DetailsContainer, detail: Detail): File {
        return File("${getDetailsContainerFolder(detailsContainer).path}/${detail.uuid}")
    }

    fun getEmotionAvatarPath(event: Event): File {
        return File("${getDetailsContainerFolder(event).path}/avatar")
    }

    /**
     * Returns the local path where one would save the emotionAvatar for an event.
     */
    fun getLocalEmotionAvatarPath(event: Event): File {
        return File(context.filesDir, getEmotionAvatarPath(event).path)
    }

    /**
     * Returns the local path where one would save an event's detail for an event.
     */
    fun getLocalDetailPath(detailsContainer: DetailsContainer, detail: Detail): File {
        return File(context.filesDir, getDetailPath(detailsContainer, detail).path)
    }

    fun getLocalDetailPath(detail: Detail): File {
        return File(context.filesDir, detail.file.path)
    }
}