package be.hogent.faith.storage

import android.content.Context
import android.graphics.Bitmap
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
            val storedFile = File(createEventImageFolder(event), "$fileName.PNG")
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

    fun createDetailSaveFile(event: Event): File {
        return File(createEventImageFolder(event), "${createSaveFileName()}.png")
    }

    /**
     * Returns a saveFile with the name in the following format:
     * day_month_year_hour_minute_second_millis
     */
    private fun createSaveFileName(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d_M_y_Ka_m_s_A"))
    }
}