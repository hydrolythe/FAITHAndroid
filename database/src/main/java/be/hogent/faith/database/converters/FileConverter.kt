package be.hogent.faith.database.converters

import java.io.File

class FileConverter {

    fun toString(file: File): String {
        return file.path
    }

    fun toFile(path: String): File {
        return File(path)
    }
}