package be.hogent.faith.storage

import android.content.Context
import be.hogent.faith.database.encryption.EncryptedDetailEntity
import be.hogent.faith.database.encryption.EncryptedEventEntity
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
     * Returns the path in which a user's events will be saved.
     */
    fun getEventFolder(event: Event): File {
        return File("users/${user!!.uid}/events/${event.uuid}")
    }

    fun getEventFolder(encryptedEventEntity: EncryptedEventEntity): File {
        return File("users/${user!!.uid}/events/${encryptedEventEntity.uuid}")
    }

    /**
     * Returns the path in which an event's detail will be saved
     */
    fun getDetailPath(event: EncryptedEventEntity, detail: EncryptedDetailEntity): File {
        return File("${getEventFolder(event).path}/${detail.uuid}")
    }
    /**
     * Returns the path in which an event's detail will be saved
     */
    fun getDetailPath(event: Event, detail: Detail): File {
        return File("${getEventFolder(event).path}/${detail.uuid}")
    }

    fun getEmotionAvatarPath(encryptedEventEntity: EncryptedEventEntity): File {
        return File("${getEventFolder(encryptedEventEntity).path}/avatar")
    }

    fun getEmotionAvatarPath(event: Event): File {
        return File("${getEventFolder(event).path}/avatar")
    }

    /**
     * Returns the local path where one would save the emotionAvatar for an event.
     */
    fun getLocalEmotionAvatarPath(event: Event): File {
        return File(context.filesDir, getEmotionAvatarPath(event).path)
    }

    /**
     * Returns the local path where one would save the emotionAvatar for an event.
     */
    fun getLocalEmotionAvatarPath(encryptedEventEntity: EncryptedEventEntity): File {
        return File(context.filesDir, getEmotionAvatarPath(encryptedEventEntity).path)
    }

    /**
     * Returns the local path where one would save an event's detail for an event.
     */
    fun getLocalDetailPath(event: Event, detail: Detail): File {
        return File(context.filesDir, getDetailPath(event, detail).path)
    }

    fun getLocalDetailPath(detail: Detail): File {
        return File(context.filesDir, detail.file.path)
    }
    fun getLocalDetailPath(encryptedDetailEntity: EncryptedDetailEntity): File {
        return File(context.filesDir, encryptedDetailEntity.file)
    }
}