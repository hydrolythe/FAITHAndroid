package be.hogent.faith.faith.util.factory

import java.io.File

object FileFactory {
    fun makeFile(): File {
        return File("path/bestand")
    }
}