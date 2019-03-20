package be.hogent.faith.storage

import android.content.Context
import android.graphics.Bitmap
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.Event
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

/**
 * Repository providing access to both the internal and external storage.
 * It decides which one will be used based on the user's settings.
 *
 * TODO: Currently only supports internal storage.
 */
const val PICTURE_EXTENSION = "png"

class StorageRepository(private val context: Context) {

    /**
     * Stores the bitmap on the device's storage.
     * It will be put in the context.filesDir/event.uuid/images/ folder
     *
     * @param fileName under which the bitmap will be saved
     * @return a Single<File> with the path derived from the event's dateTime
     */
    fun storeBitmap(bitmap: Bitmap, event: Event, fileName: String): Single<File> {
        return Single.fromCallable {
            val storedFile = File(createEventImageFolder(event), "$fileName.$PICTURE_EXTENSION")
            storedFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            storedFile
        }
    }

    private fun createEventImageFolder(event: Event): File {
        val imageDirectory = File(context.filesDir, "events/${event.uuid}/images")
        imageDirectory.mkdirs()
        return imageDirectory
    }

    private fun createEventAudioFolder(event: Event): File {
        val imageDirectory = File(context.filesDir, "events/${event.uuid}/audio")
        imageDirectory.mkdirs()
        return imageDirectory
    }

    /**
     * Returns a saveFile with the name in the following format:
     * day_month_year_hour_minute_second_millis
     */
    private fun createSaveFileName(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d_M_y_k_m_s_A"))
    }

    /**
     * Stores a file (probably an [Event]'s [Detail] by moving it from a temporary file in the device's cache
     * directory to permanent storage.
     *
     * @param tempStorageFile the (cache) file in which the recording is currently stored
     */
    fun storeAudioRecording(tempStorageFile: File, event: Event): Single<File> {
        return Single.fromCallable {
            val storedFile = File(createEventAudioFolder(event), createSaveFileName())
            tempStorageFile.copyTo(target = storedFile, overwrite = true)
            tempStorageFile.delete()
            storedFile
        }
    }

    /**
     * Returns a saveFile with the name in the following format:
     * [name]-day_month_year_hour_minute_second_millis
     *
     * @param name An optional name that will be prepended to the timestamp
     */
    private fun createSaveFileName(name: String): String {
        return name + LocalDateTime.now().format(DateTimeFormatter.ofPattern("M-y-k_m_s_A"))
    }

    /**
     *  Moves a photo detail from an [event] from the [tempPhotoFile] where it is currently stored to
     *  a permanent location on the device's storage.
     *
     * @param tempPhotoFile the [File] where the photo is now. Probably somewhere in the cache.
     * @param event the [Event] this photo will be added to as a detail (not by this function).
     *          Used to store the photo in a folder specific for the event.
     * @param photoName an optional name for the photo. Will be used for the filename.
     */
    fun movePhotoFromTempStorage(tempPhotoFile: File, event: Event, photoName: String = "photo"): Single<File> {
        return Single.fromCallable {
            val storedFile = File(createEventImageFolder(event), "${createSaveFileName(photoName)}.$PICTURE_EXTENSION")
            tempPhotoFile.copyTo(target = storedFile, overwrite = true)
            tempPhotoFile.delete()
            storedFile
        }
    }
}