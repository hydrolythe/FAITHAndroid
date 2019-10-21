package be.hogent.faith.database.factory

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.util.factory.DataFactory
import java.util.UUID

/**
 * Used in addition to the Factories included in the util package.
 */
// We can't put these methods here because the util package doesn't know the database package.
// Making it depend on the database module would introduce a circular dependency.
object EntityFactory {
    fun makeDetailEntity(eventUuid: UUID): DetailEntity {
        val rand = Math.random()
        return when {
            rand < 0.33 -> TextDetailEntity(
                DataFactory.randomFile(), DataFactory.randomUUID(), eventUuid
            )
            rand < 0.66 -> PictureDetailEntity(
                DataFactory.randomFile(), DataFactory.randomUUID(), eventUuid
            )
            else -> AudioDetailEntity(
                DataFactory.randomFile(), DataFactory.randomUUID(), eventUuid
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

    fun makeEventEntity(
        userUuid: UUID = DataFactory.randomUUID(),
        uuid: UUID = DataFactory.randomUUID()
    ): EventEntity {
        return EventEntity(
            DataFactory.randomDateTime(),
            DataFactory.randomString(),
            DataFactory.randomFile(),
            DataFactory.randomString(),
            uuid,
            userUuid
        )
    }

    fun makeEventWithDetailsList(count: Int, userUuid: UUID): List<EventWithDetails> {
        val events = mutableListOf<EventWithDetails>()
        repeat(count) {
            events.add(makeEventWithDetailsEntity(userUuid))
        }
        return events
    }

    fun makeEventWithDetailsEntity(
        userUuid: UUID = DataFactory.randomUUID(),
        nbrOfDetails: Int = 5
    ): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = makeEventEntity(userUuid)
            it.addDetailEntities(makeDetailEntityList(nbrOfDetails, it.eventEntity.uuid))
        }
    }

    fun makeUserEntity(): UserEntity {
        return UserEntity(
            DataFactory.randomUUID(), DataFactory.randomString(), DataFactory.randomString()
        )
    }
}