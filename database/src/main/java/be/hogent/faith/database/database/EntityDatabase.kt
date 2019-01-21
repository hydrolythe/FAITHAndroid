package be.hogent.faith.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.hogent.faith.database.converters.DetailTypeConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.converters.UuidConverter
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.EventEntity

@Database(
    entities = [
        EventEntity::class,
        DetailEntity::class],
    version = 1
)
@TypeConverters(
    value = [
        DetailTypeConverter::class,
        LocalDateTimeConverter::class,
        UuidConverter::class]
)
internal abstract class EntityDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun detailDao(): DetailDao

    companion object {
        @Volatile
        private var INSTANCE: EntityDatabase? = null
        const val DATABASE_NAME = "ENTITY_DATABSE"

        fun getDatabase(context: Context): EntityDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EntityDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
