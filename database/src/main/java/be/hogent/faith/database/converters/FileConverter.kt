package be.hogent.faith.database.converters

import androidx.room.TypeConverter
import java.io.File

class FileConverter {

    @TypeConverter
    fun toString(file: File): String {
        return file.path
    }

    @TypeConverter
    fun toFile(path: String): File {
        return File(path)
    }
}