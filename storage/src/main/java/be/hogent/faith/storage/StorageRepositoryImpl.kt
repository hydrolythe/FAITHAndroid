package be.hogent.faith.storage

import android.content.Context
import android.graphics.Bitmap
import org.threeten.bp.LocalDateTime
import java.io.File

/**
 * Repository providing access to both the internal and external storage.
 * It decides which one will be used based on the user's settings.
 *
 * TODO: Currently only supports internal storage.
 */
class StorageRepositoryImpl(private val context: Context) {

    fun storeBitmap(bitmap: Bitmap): File {
        val storedFile = createNewFile()
        context.openFileOutput(storedFile.absolutePath, Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return storedFile
    }

    private fun createNewFile(): File {
        return File(context.filesDir, LocalDateTime.now().toString())
    }

}