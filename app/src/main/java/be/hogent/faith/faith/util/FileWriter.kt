package be.hogent.faith.faith.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class FileWriter {

    @Throws(IOException::class)
    fun writeBitMapToFile(bitmap: Bitmap, context: Context) {
        val fileName = createFileName()
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun createFileName(): String {
        return UUID.randomUUID().toString()
    }
}