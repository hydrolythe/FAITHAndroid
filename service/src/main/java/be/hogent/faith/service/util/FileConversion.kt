package be.hogent.faith.service.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Base64
import java.io.ByteArrayOutputStream

const val THUMBNAIL_WIDTH = 60
const val THUMBNAIL_HEIGHT = 60

fun getThumbnail(path: String) : Bitmap{
    return  ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
}

fun getThumbnail(bitmap:Bitmap) : Bitmap{
    return  ThumbnailUtils.extractThumbnail(bitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
}

fun base64encodeImage(bm: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}