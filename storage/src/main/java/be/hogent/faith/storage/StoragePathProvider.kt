package be.hogent.faith.storage

import android.content.Context
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedEvent
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
 * - Backpack files: users/[User.uuid]/containers/backpack/[Detail.uuid]
 */
class StoragePathProvider(
    private val context: Context,
    private val fbAuth: FirebaseAuth
) {

    private val user: FirebaseUser?
        get() = fbAuth.currentUser

    /**
     * Returns the folder path in which a user's detailsContainer will be saved.
     */
    fun detailsContainerFolderPath(detailsContainer: DetailsContainer): File {
        return when (detailsContainer) {
            is Backpack -> File("users/${user!!.uid}/containers/backpack")
            is Cinema -> File("users/${user!!.uid}/containers/cinema")
        }
    }

    /**
     * Returns the **relative** path in which a detail will be saved.
     * Should usually be prefixed with [temporaryStorage] or [localStorage] to be a valid path.
     */
    fun detailPath(detail: EncryptedDetail, detailsContainer: DetailsContainer): File {
        return File("${detailsContainerFolderPath(detailsContainer).path}/${detail.uuid}")
    }

    /**
     * Returns the **relative** path in which a detail will be saved.
     * Should usually be prefixed with [temporaryStorage] or [localStorage] to be a valid path.
     */
    fun detailPath(detail: Detail, detailsContainer: DetailsContainer): File {
        return File("${detailsContainerFolderPath(detailsContainer).path}/${detail.uuid}")
    }

    /**
     * Returns the **relative** path in which a detail will be saved.
     * Should usually be prefixed with [temporaryStorage] or [localStorage] to be a valid path.
     */
    fun detailPath(detail: Detail, event: Event): File {
        return File("${eventsFolderPath(event).path}/${detail.uuid}")
    }

    /**
     * Returns the **relative** path in which a detail will be saved.
     * Should usually be prefixed with [temporaryStorage] or [localStorage] to be a valid path.
     */
    fun detailPath(encryptedEvent: EncryptedEvent, encryptedDetail: EncryptedDetail): File {
        return File("${eventsFolderPath(encryptedEvent).path}/${encryptedDetail.uuid}")
    }

    private fun eventsFolderPath(event: Event): File {
        return File("users/${user!!.uid}/events/${event.uuid}")
    }

    private fun eventsFolderPath(encryptedEvent: EncryptedEvent): File {
        return File("users/${user!!.uid}/events/${encryptedEvent.uuid}")
    }

    /**
     * Returns the **relative** path in which an [encryptedEvent]s emotionAvatar will be saved.
     * Should usually be prefixed with [temporaryStorage] or [localStorage] to be a valid path.
     */
    fun emotionAvatarPath(encryptedEvent: EncryptedEvent): File {
        return File("${eventsFolderPath(encryptedEvent).path}/emotionAvatar")
    }

    /**
     * Returns the **relative** path in which an [event]s emotionAvatar will be saved.
     * Should usually be prefixed with [temporaryStorage] or [localStorage] to be a valid path on the device.
     */
    fun emotionAvatarPath(event: Event): File {
        return File("${eventsFolderPath(event).path}/emotionAvatar")
    }

    /**
     * Given a [file]  with a  relative path, returns a version of this path in the device's local storage.
     */
    fun localStorage(file: File): File {
        return File(context.filesDir, file.path)
    }

    /**
     * Given a [file] with a relative path, returns a version of this path in the device's temporary storage.
     */
    fun temporaryStorage(file: File): File {
        return File(context.cacheDir, file.path)
    }
}