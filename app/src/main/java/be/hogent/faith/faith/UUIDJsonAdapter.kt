package be.hogent.faith.faith

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.UUID

class UUIDJsonAdapter {
    @ToJson
    fun toJson(uid: UUID):String{
        return uid.toString()
    }
    @FromJson
    fun fromJson(uid:String):UUID{
        return UUID.fromString(uid)
    }
}