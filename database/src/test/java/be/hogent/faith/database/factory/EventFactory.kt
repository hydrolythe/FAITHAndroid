package be.hogent.faith.database.factory

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import java.util.UUID

object EventFactory {

    fun makeEvent(nbrOfDetails: Int = 5): Event {
        val event = Event(
            DataFactory.randomDateTime(),
            DataFactory.randomString(),
            DataFactory.randomFile(),
            DataFactory.randomUID()
        )
        repeat(nbrOfDetails) {
            event.addDetail(DetailFactory.makeDetail())
        }
        return event
    }

    fun makeEventEntity(userUuid: UUID = DataFactory.randomUID()): EventEntity {
        return EventEntity(
            DataFactory.randomDateTime(),
            DataFactory.randomString(),
            DataFactory.randomFile(),
            DataFactory.randomUID(),
            userUuid
        )
    }

    fun makeEventWithDetailsEntity(userUuid: UUID = DataFactory.randomUID(), nbrOfDetails: Int = 5): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = makeEventEntity(userUuid)
            it.detailEntities = DetailFactory.makeDetailEntityList(nbrOfDetails, it.eventEntity.uuid)
        }
    }

    fun makeEventWithDetailsList(count: Int, userUuid: UUID): List<EventWithDetails> {
        val events = mutableListOf<EventWithDetails>()
        repeat(count) {
            events.add(makeEventWithDetailsEntity(userUuid))
        }
        return events
    }

    fun makeEventList(count: Int): List<Event> {
        val events = mutableListOf<Event>()
        repeat(count) {
            events.add(makeEvent())
        }
        return events
    }
}
