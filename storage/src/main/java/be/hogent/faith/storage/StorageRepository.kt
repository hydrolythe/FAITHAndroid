package be.hogent.faith.storage

import android.content.Context
import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

const val EMOTION_AVATAR_FILENAME = "emotionAvatar"

/**
 * Repository providing access to both the internal and external storage.
 * It decides which one will be used based on the user's settings.
 *
 * TODO: Currently only supports internal storage.
 */
const val TEXT_EXTENSION = "txt"

class StorageRepository(private val context: Context) {

    /**
     * Stores the bitmap on the device's storage.
     * It will be put in the context.filesDir/event.uuid/images/ folder
     *
     * @param fileName under which the bitmap will be saved
     * @return a Single<File> with the path derived from the event's dateTime
     */
    fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File> {
        return Single.fromCallable {
            val storedFile = File(folder, fileName)
            storedFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            storedFile
        }
    }

    fun deleteFile(file: File): Boolean {
        return file.delete()
    }

    private fun getEventImageDirectory(event: Event): File {
        val imageDirectory = File(getEventDirectory(event), "images")
        imageDirectory.mkdirs()
        return imageDirectory
    }

    private fun getEventAudioDirectory(event: Event): File {
        val audioDir = File(getEventDirectory(event), "audio")
        audioDir.mkdirs()
        return audioDir
    }

    private fun getEventDirectory(event: Event): File {
        val eventDir = File(context.filesDir, "events/${event.uuid}")
        eventDir.mkdirs()
        return eventDir
    }

    private fun getEventTextDirectory(event: Event): File {
        val textDir = File(context.filesDir, "text/${event.uuid}")
        textDir.mkdirs()
        return textDir
    }

    fun saveEventAudio(tempStorageFile: File, event: Event): Single<File> {
        return moveFileFromTempStorageToPermanentStorage(
            tempStorageFile,
            getEventAudioDirectory(event),
            createSaveFileName()
        )
    }

    fun saveEventDrawing(bitmap: Bitmap, event: Event): Single<File> {
        return storeBitmap(
            bitmap,
            getEventImageDirectory(event),
            createSaveFileName()
        )
    }

    fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File> {
        return moveFileFromTempStorageToPermanentStorage(
            tempStorageFile,
            getEventImageDirectory(event),
            createSaveFileName()
        )
    }

    fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File> {
        return storeBitmap(
            bitmap,
            getEventDirectory(event),
            EMOTION_AVATAR_FILENAME
        )
    }

    /**
     * Returns a saveFile with the name in the following format:
     * day_month_year_hour_minute_second_millis
     */
    private fun createSaveFileName(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d_M_y_k_m_s_A"))
    }

    /**
     * Stores a file  by moving it from a temporary file in the device's cache directory to permanent storage.
     *
     * @param tempStorageFile the (cache) file in which the recording is currently stored
     * @param folder the folder where the file should be stored
     */
    private fun moveFileFromTempStorageToPermanentStorage(
        tempStorageFile: File,
        folder: File,
        fileName: String
    ): Single<File> {
        return Single.fromCallable {
            val storedFile = File(folder, fileName)
            tempStorageFile.copyTo(target = storedFile, overwrite = true)
//            tempStorageFile.delete()
            storedFile
        }
    }

    /**
     *  Writes a String to a text file
     *
     * @param text the String
     * @param event the [Event] this text will be added to as a detail (not by this function).
     *          Used to store the text in a folder specific for the event.
     */
    fun saveText(text: String, event: Event): Single<File> {
        return Single.fromCallable {
            val storedFile =
                File(getEventTextDirectory(event), "${createSaveFileName()}.$TEXT_EXTENSION")
            storedFile.writeText(text)
            storedFile
        }
    }

    fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return Single.fromCallable {
            val readString = textDetail.file.readText()
            readString
        }
    }

    fun overWriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return Completable.fromCallable {
            existingDetail.file.writeText(text)
        }
    }
}