package be.hogent.faith.storage

import android.content.Context
import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.IOException

/**
 * Repository providing access to both the internal and external storage.
 * It decides which one will be used based on the user's settings.
 *
 * TODO: Currently only supports internal storage.
 */
class StorageRepository(private val context: Context) {

    fun storeBitmap(bitmap: Bitmap, event: Event): Single<File> {
        return try {
            val storedFile =
                File(
                    createEventImageFolder(event),
                    "${event.dateTime.format(DateTimeFormatter.ofPattern("yMdHms"))}.PNG"
                )
            storedFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Single.fromObservable(Observable.just(storedFile))
        } catch (e: IOException) {
            Single.error(e)
        }
    }

    private fun createEventImageFolder(event: Event): File {
        val imageDirectory = File(context.filesDir, "events/${event.uuid}/images")
        imageDirectory.mkdirs()
        return imageDirectory
    }
}