package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event
import java.util.UUID

class EventMapper : MapperWithForeignKey<EventEntity, Event> {

    override fun mapFromEntity(entity: EventEntity): Event {
        return Event(
            dateTime = entity.dateTime,
            title = entity.title,
            notes = entity.notes,
            emotionAvatar = entity.emotionAvatar,
            uuid = entity.uuid
        )
    }

    override fun mapToEntity(model: Event, foreignKey: UUID): EventEntity {
        return EventEntity(
            dateTime = model.dateTime,
            title = model.title!!,
            notes = model.notes,
            emotionAvatar = model.emotionAvatar,
            uuid = model.uuid,
            userUuid = foreignKey
        )
    }

    override fun mapFromEntities(entities: List<EventEntity>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>, foreignKey: UUID): List<EventEntity> {
        return models.map { mapToEntity(it, foreignKey) }
    }
}
