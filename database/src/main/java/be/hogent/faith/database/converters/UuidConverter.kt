package be.hogent.faith.database.converters

import androidx.room.TypeConverter
import java.util.UUID

internal class UuidConverter {

    @TypeConverter
    fun toString(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUuid(uuidString: String): UUID {
        return UUID.fromString(uuidString)
    }
}