package be.hogent.faith.database.converters

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class LocalDateTimeConverter {

        @TypeConverter
        fun toDate(value: String): LocalDateTime {
            return LocalDateTime.parse(value)
        }

        @TypeConverter
        fun toString(value: LocalDateTime): String {
            return value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
}