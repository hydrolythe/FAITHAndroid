package be.hogent.faith.faith

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.LocalDateTime

class DateJsonAdapter {
    @ToJson
    fun toJson(date: LocalDateTime):String{
        return date.toString()
    }
    @FromJson
    fun fromJson(string:String):LocalDateTime{
        return LocalDateTime.parse(string)
    }
}