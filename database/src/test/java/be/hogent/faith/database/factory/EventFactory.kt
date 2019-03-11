package be.hogent.faith.database.factory

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event

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

    fun makeEventEntity(): EventEntity {
        return EventEntity(
            DataFactory.randomDateTime(),
            DataFactory.randomString(),
            DataFactory.randomFile(),
            DataFactory.randomUID()
        )
    }

    fun makeEventWithDetailsEntity(nbrOfDetails: Int = 5): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = makeEventEntity()
            it.detailEntities = DetailFactory.makeDetailEntityList(nbrOfDetails, it.eventEntity.uuid)
        }
    }
}
