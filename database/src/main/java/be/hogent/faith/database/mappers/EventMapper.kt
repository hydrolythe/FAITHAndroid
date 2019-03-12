package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event

class EventMapper : Mapper<EventEntity, Event> {

    override fun mapFromEntity(entity: EventEntity): Event {
        return Event(
            entity.dateTime,
            entity.title,
            entity.emotionAvatar,
            entity.uuid
        )
    }

    override fun mapToEntity(model: Event): EventEntity {
        return EventEntity(
            model.dateTime,
            model.title!!,
            model.emotionAvatar,
            model.uuid
        )
    }

    override fun mapFromEntities(entities: List<EventEntity>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>): List<EventEntity> {
        return models.map(this::mapToEntity)
    }
}
