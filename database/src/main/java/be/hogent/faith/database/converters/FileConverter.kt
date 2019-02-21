package be.hogent.faith.database.converters

import androidx.room.TypeConverter
import java.io.File

class FileConverter {

    @TypeConverter
    fun toString(file: File): String {
        return file.absolutePath
    }

    @TypeConverter
    fun toFile(absolutePath: String): File {
        return File(absolutePath)
    }
}