package be.hogent.faith.database.factory

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.FilmEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.util.factory.DataFactory
import java.util.UUID

/**
 * Used in addition to the Factories included in the util package.
 */
// We can't put these methods here because the util package doesn't know the database package.
// Making it depend on the database module would introduce a circular dependency.
object EntityFactory {

    fun makeDetailEntity(): DetailEntity {
        val rand = Math.random()
        return when {
            rand < 0.25 -> DetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "",
                DetailType.PHOTO
            )
            rand < 0.50 -> DetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "",
                DetailType.DRAWING
            )
            rand < 0.75 -> DetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "",
                DetailType.AUDIO
            )
            else -> DetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "",
                DetailType.TEXT
            )
        }
    }

    fun makeDetailEntityList(count: Int): List<DetailEntity> {
        val details = mutableListOf<DetailEntity>()
        repeat(count) {
            details.add(makeDetailEntity())
        }
        return details
    }

    fun makeEventEntity(uuid: UUID = DataFactory.randomUUID()): EventEntity {
        return EventEntity(
            LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
            DataFactory.randomString(),
            FileConverter().toString(DataFactory.randomFile()),
            DataFactory.randomString(),
            uuid.toString()
        )
    }

    fun makeEventEntityListWithDetails(count: Int): List<EventEntity> {
        val events = mutableListOf<EventEntity>()
        repeat(count) {
            events.add(makeEventEntityWithDetails())
        }
        return events
    }

    fun makeEventEntityWithDetails(nbrOfDetails: Int = 5): EventEntity {
        return EventEntity(
            LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
            DataFactory.randomString(),
            FileConverter().toString(DataFactory.randomFile()),
            DataFactory.randomString(),
            DataFactory.randomUUID().toString(),
            makeDetailEntityList(nbrOfDetails)
        )
    }

    fun makeUserEntity(): UserEntity {
        return UserEntity(
            DataFactory.randomUUID().toString(),
            DataFactory.randomString(),
            DataFactory.randomString()
        )
    }

    fun makeFilmEntity(): FilmEntity {
        return FilmEntity(DataFactory.randomString(), DataFactory.randomString(), DataFactory.randomString(), DataFactory.randomUUID().toString())
    }

    fun makeFilmEntityList(count: Int): List<FilmEntity> {
        val films = mutableListOf<FilmEntity>()
        repeat(count) {
            films.add(makeFilmEntity())
        }
        return films
    }
}
