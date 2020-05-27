package be.hogent.faith.database.factory

import be.hogent.faith.database.common.EncryptedDetailEntity
import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.event.EncryptedEventEntity
import be.hogent.faith.database.goal.EncryptedGoalEntity
import be.hogent.faith.database.user.UserEntity
import be.hogent.faith.util.factory.DataFactory
import java.util.UUID

/**
 * Used in addition to the Factories included in the util package.
 */
object EntityFactory {

    fun makeDetailEntity(): EncryptedDetailEntity {
        val rand = Math.random()
        return when {
            rand < 0.25 -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "photo"
            )
            rand < 0.50 -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "drawing"
            )
            rand < 0.75 -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
                DataFactory.randomUUID().toString(),
                "audio"
            )
            else -> EncryptedDetailEntity(
                FileConverter().toString(DataFactory.randomFile()),
                "",
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

    fun makeGoal(uuid: UUID = DataFactory.randomUUID()): EncryptedGoalEntity {
        return EncryptedGoalEntity(
            dateTime = LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
            description = DataFactory.randomString(),
            uuid = uuid.toString(),
            isCompleted = DataFactory.randomBoolean(),
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
