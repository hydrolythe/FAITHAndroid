package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event
import java.util.UUID

class EventMapper : MapperWithForeignKey<EventEntity, Event> {

    override fun mapFromEntity(entity: EventEntity): Event {
        return Event(
            entity.dateTime,
            entity.title,
            entity.emotionAvatar,
            entity.uuid
        )
    }

    override fun mapToEntity(model: Event, foreignKey: UUID): EventEntity {
        return EventEntity(
            model.dateTime,
            model.title!!,
            model.emotionAvatar,
            model.uuid,
            foreignKey
        )
    }

    override fun mapFromEntities(entities: List<EventEntity>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>, foreignKey: UUID): List<EventEntity> {
        return models.map { mapToEntity(it, foreignKey) }
    }
}
