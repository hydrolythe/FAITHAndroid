package be.hogent.faith.database.converters

import androidx.room.TypeConverter
import be.hogent.faith.database.models.DetailTypeEntity

class DetailTypeConverter {

    @TypeConverter
    fun toDetailTypeEntity(id: Int): DetailTypeEntity {
        return DetailTypeEntity.values()[id]
    }

    @TypeConverter
    fun toInteger(detailTypeEntity: DetailTypeEntity): Int {
        return detailTypeEntity.id
    }
}