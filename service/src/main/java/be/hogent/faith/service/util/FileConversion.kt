package be.hogent.faith.service.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

const val THUMBNAIL_WIDTH = 60
const val THUMBNAIL_HEIGHT = 60

internal fun Bitmap.getThumbnail(): Bitmap {
    return ThumbnailUtils.extractThumbnail(this, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
}

internal fun Bitmap.base64encodeImage(): String {
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

internal fun File.getThumbnail(): Bitmap {
    return BitmapFactory.decodeFile(this.path).getThumbnail()
}