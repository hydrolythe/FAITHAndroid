package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.User
import java.io.File

object FileFactory {
    fun makeFile(): File {
       return File("path/bestand")
    }
}