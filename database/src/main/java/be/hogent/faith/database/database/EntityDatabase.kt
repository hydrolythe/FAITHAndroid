package be.hogent.faith.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.converters.UuidConverter
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.daos.UserDao
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity

@Database(
    entities = [
        EventEntity::class,
        DetailEntity::class,
        AudioDetailEntity::class,
        PictureDetailEntity::class,
        TextDetailEntity::class,
        UserEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    value = [
        LocalDateTimeConverter::class,
        FileConverter::class,
        UuidConverter::class]
)
abstract class EntityDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun detailDao(): DetailDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: EntityDatabase? = null
        private const val DATABASE_NAME = "ENTITY_DATABASE"

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
