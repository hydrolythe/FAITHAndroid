package be.hogent.faith.util

import java.io.File

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun String.withSuffix(suffix: String): String {
    return this + suffix
}

// From: https://stackoverflow.com/questions/27379059/determine-if-two-files-store-the-same-content
fun File.contentEqual(other: File): Boolean {
    if (this.length() != other.length()) return false

    if (this.length() < 4096) {
        return this.readBytes().contentEquals(other.readBytes())
    }

    this.inputStream().use { thisStream ->
        other.inputStream().use { otherStream ->
            val bufferThis = ByteArray(16_000 * Byte.SIZE_BYTES)
            val bufferOther = ByteArray(16_000 * Byte.SIZE_BYTES)
            while (thisStream.read(bufferThis) != -1) {
                otherStream.read(bufferOther)
                if (!bufferThis.contentEquals(bufferOther)) {
                    return false
                }
            }
        }
    }
    return true
}
