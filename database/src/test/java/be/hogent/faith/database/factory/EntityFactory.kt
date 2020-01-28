package be.hogent.faith.database.factory

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.database.models.EncryptedEventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.util.factory.DataFactory
import java.util.UUID

/**
 * Used in addition to the Factories included in the util package.
 */
// We can't put these methods here because the util package doesn't know the database package.
// Making it depend on the database module would introduce a circular dependency.
object EntityFactory {

    fun makeDetailEntity(): EncryptedDetailEntity {
        val rand = Math.random()
        return when {
            rand < 0.25 -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                DataFactory.randomUUID().toString(),
                "photo"
            )
            rand < 0.50 -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                DataFactory.randomUUID().toString(),
                "drawing"
            )
            rand < 0.75 -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                DataFactory.randomUUID().toString(),
                "audio"
            )
            else -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                DataFactory.randomUUID().toString(),
                "text"
            )
        }
    }

    fun makeDetailEntityList(count: Int): List<EncryptedDetailEntity> {
        val details = mutableListOf<EncryptedDetailEntity>()
        repeat(count) {
            details.add(makeDetailEntity())
        }
        return details
    }

    fun makeEventEntity(uuid: UUID = DataFactory.randomUUID()): EncryptedEventEntity {
        return EncryptedEventEntity(
            dateTime = LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
            title = DataFactory.randomString(),
            emotionAvatar = FileConverter().toString(DataFactory.randomFile()),
            notes = DataFactory.randomString(),
            uuid = uuid.toString(),
            encryptedStreamingDEK = "sDEK",
            encryptedDEK = "DEK"
        )
    }

    fun makeEventEntityListWithDetails(count: Int): List<EncryptedEventEntity> {
        val events = mutableListOf<EncryptedEventEntity>()
        repeat(count) {
            events.add(makeEventEntityWithDetails())
        }
        return events
    }

    fun makeEventEntityWithDetails(nbrOfDetails: Int = 5): EncryptedEventEntity {
        return EncryptedEventEntity(
            dateTime = LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
            title = DataFactory.randomString(),
            emotionAvatar = FileConverter().toString(DataFactory.randomFile()),
            notes = DataFactory.randomString(),
            uuid = DataFactory.randomUUID().toString(),
            details = makeDetailEntityList(nbrOfDetails),
            encryptedStreamingDEK = "sDEK",
            encryptedDEK = "DEK"
        )
    }

    fun makeUserEntity(): UserEntity {
        return UserEntity(
            DataFactory.randomUUID().toString(),
            DataFactory.randomString(),
            DataFactory.randomString()
        )
    }
}
