package be.hogent.faith.database.factory

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.DataFactory.randomString
import java.util.UUID

/**
 * Used in addition to the Factories included in the util package.
 */
// We can't put these methods here because the util package doesn't know the database package.
// Making it depend on the database module would introduce a circular dependency.
object EntityFactory {
 /*
    fun makeDetailEntity(eventUuid: UUID): DetailEntity {
        val rand = Math.random()
        return when {
            rand < 0.33 ->DetailEntity(
                DataFactory.randomFile(), randomString(), DataFactory.randomUUID(), 1
            )
            rand < 0.66 -> PictureDetailEntity(
                DataFactory.randomFile(), randomString(), DataFactory.randomUUID(), 2
            )
            else -> AudioDetailEntity(
                DataFactory.randomFile(), randomString(), DataFactory.randomUUID(), 3
            )
        }
    }

    fun makeDetailEntityList(count: Int, eventUuid: UUID): List<DetailEntity> {
        val details = mutableListOf<DetailEntity>()
        repeat(count) {
            details.add(makeDetailEntity(eventUuid))
        }
        return details
    }
*/
    fun makeEventEntity(
        userUuid: UUID = DataFactory.randomUUID(),
        uuid: UUID = DataFactory.randomUUID()
    ): EventEntity {
        return EventEntity(
            LocalDateTimeConverter().toString(DataFactory.randomDateTime()),
            DataFactory.randomString(),
            FileConverter().toString(DataFactory.randomFile()),
            DataFactory.randomString(),
            uuid.toString()

        )
    }

    /*
    fun makeEventWithDetailsList(count: Int, userUuid: UUID): List<EventWithDetails> {
        val events = mutableListOf<EventWithDetails>()
        repeat(count) {
            events.add(makeEventWithDetailsEntity(userUuid))
        }
        return events
    }

    fun makeEventWithDetailsEntity(userUuid: UUID = DataFactory.randomUUID(), nbrOfDetails: Int = 5): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = makeEventEntity(userUuid)
            it.addDetailEntities(makeDetailEntityList(nbrOfDetails, it.eventEntity.uuid))
        }
    }
*/
    fun makeUserEntity(): UserEntity {
        return UserEntity(
            DataFactory.randomUUID().toString(), DataFactory.randomString(), DataFactory.randomString()
        )
    }


}