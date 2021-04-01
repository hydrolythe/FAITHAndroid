package be.hogent.faith.faith

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File

class FileJsonAdapter {
    @ToJson
    fun toJson(file: File):String{
        return file.absolutePath
    }
    @FromJson
    fun fromJson(string:String):File{
        return File(string)
    }
}