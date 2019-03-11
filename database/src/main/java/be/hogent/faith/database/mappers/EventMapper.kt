package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event

class EventMapper : Mapper<EventEntity, Event> {

    override fun mapFromEntity(entity: EventEntity): Event {
        val event = Event(entity.dateTime, entity.title, entity.uuid)
        val detailMapper = DetailMapper(event)
        entity.details.forEach {
            event.addDetail(detailMapper.mapFromEntity(it))
        }
        return event
    }

    override fun mapToEntity(model: Event): EventEntity {
        // From here on the title of the [Event] has to be filled in
        val eventEntity = EventEntity(model.dateTime, model.title!!, model.uuid)
        val detailMapper = DetailMapper(model)
        model.details.forEach {
            eventEntity.details.add(detailMapper.mapToEntity(it))
        }
        return eventEntity
    }

    override fun mapFromEntities(entities: List<EventEntity>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>): List<EventEntity> {
        return models.map(this::mapToEntity)
    }
}
