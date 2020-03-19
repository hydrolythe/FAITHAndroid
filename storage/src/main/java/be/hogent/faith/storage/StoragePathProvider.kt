package be.hogent.faith.storage

import android.content.Context
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedEvent
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File

/**
 * This class holds the conventions on the file structure of the different objects (events, details,...)
 * It provides utility functions that returns the correct path for these objects.
 *
 * These are the conventions:
 * - Events main folder: users/[User.uuid]/events/[Event.uuid]/
 * - Event's emotionAvatar: users/[User.uuid]/events/[Event.uuid]/emotionAvatar
 * - Detail inside an event: users/[User.uuid]/events/[Event.uuid]/[Detail.uuid]
 */
class StoragePathProvider(
    private val context: Context,
    private val fbAuth: FirebaseAuth
) {

    private val user: FirebaseUser?
        get() = fbAuth.currentUser

    /**
     * Returns the path in which a user's events will be saved.
     */
    fun getEventFolder(event: Event): File {
        return File("users/${user!!.uid}/events/${event.uuid}")
    }

    fun getEventFolder(encryptedEvent: EncryptedEvent): File {
        return File("users/${user!!.uid}/events/${encryptedEvent.uuid}")
    }

    /**
     * Returns the path in which an event's detail will be saved
     */
    fun getDetailPath(encryptedEvent: EncryptedEvent, encryptedDetail: EncryptedDetail): File {
        return File("${getEventFolder(encryptedEvent).path}/${encryptedDetail.uuid}")
    }

    fun getEmotionAvatarPath(encryptedEvent: EncryptedEvent): File {
        return File("${getEventFolder(encryptedEvent).path}/emotionAvatar")
    }

    fun getEmotionAvatarPath(event: Event): File {
        return File("${getEventFolder(event).path}/emotionAvatar")
    }

    /**
     * Returns the local path where one would save the emotionAvatar for an event.
     */
    fun getLocalEmotionAvatarPath(encryptedEvent: EncryptedEvent): File {
        return File(context.filesDir, getEmotionAvatarPath(encryptedEvent).path)
    }

    /**
     * Returns the local path where one would save an event's detail for an event.
     */
    fun getLocalDetailPath(event: EncryptedEvent, detail: EncryptedDetail): File {
        return File(context.filesDir, getDetailPath(event, detail).path)
    }

    fun getLocalDetailPath(detail: Detail): File {
        return File(context.filesDir, detail.file.path)
    }

    fun getLocalDetailPath(encryptedDetail: EncryptedDetail): File {
        return File(context.filesDir, encryptedDetail.file.path)
    }

    /**
     * Given a relative path, returns a version of this path in the device's local storage.
     */
    fun localStoragePath(file: File): File {
        return File(context.filesDir, file.path)
    }
}