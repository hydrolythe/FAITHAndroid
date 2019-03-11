package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event

class EventWithDetailsMapper : Mapper<EventWithDetails, Event> {

    override fun mapFromEntity(entity: EventWithDetails): Event {
        val event = EventMapper().mapFromEntity(entity.eventEntity)
        DetailMapper(event).mapFromEntities(entity.detailEntities).forEach { event.addDetail(it) }
        return event
    }

    override fun mapToEntity(model: Event): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = EventMapper().mapToEntity(model)
            it.detailEntities = DetailMapper(model).mapToEntities(model.details)
        }
    }

    override fun mapFromEntities(entities: List<EventWithDetails>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>): List<EventWithDetails> {
        return models.map(this::mapToEntity)
    }
}