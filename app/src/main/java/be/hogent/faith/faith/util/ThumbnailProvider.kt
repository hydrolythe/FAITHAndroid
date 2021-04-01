package be.hogent.faith.faith.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

const val THUMBNAIL_WIDTH = 60
const val THUMBNAIL_HEIGHT = 60

class ThumbnailProvider {
    fun getBase64EncodedThumbnail(file: File): String {
        return getBase64EncodedThumbnail(BitmapFactory.decodeFile(file.path))
    }

    fun getBase64EncodedThumbnail(bitmap: Bitmap): String {
        return encodebase64(
            ThumbnailUtils.extractThumbnail(
                ThumbnailUtils.extractThumbnail(bitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT),
                THUMBNAIL_WIDTH,
                THUMBNAIL_HEIGHT
            )
        )
    }

    private fun encodebase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}