package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event
import org.threeten.bp.DateTimeUtils.toDate
import java.util.UUID

object EventMapper : Mapper<EventEntity, Event> {

    override fun mapFromEntity(entity: EventEntity): Event {
        return Event(
            dateTime = LocalDateTimeConverter().toDate(entity.dateTime),
            title = entity.title,
            notes = entity.notes,
            emotionAvatar = entity.emotionAvatar?.let { FileConverter().toFile(it) },
            uuid = UUID.fromString(entity.uuid)
        )
    }

    override fun mapToEntity(model: Event): EventEntity {
        return EventEntity(
            dateTime = LocalDateTimeConverter().toString(model.dateTime),
            title = model.title!!,
            notes = model.notes,
            emotionAvatar = model.emotionAvatar?.path,
            uuid = model.uuid.toString()
        )
    }

    override fun mapFromEntities(entities: List<EventEntity>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>): List<EventEntity> {
        return models.map { mapToEntity(it) }
    }
}
