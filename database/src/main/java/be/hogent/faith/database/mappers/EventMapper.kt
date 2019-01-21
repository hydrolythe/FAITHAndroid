package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.domain.models.Event

//TODO: write tests
internal object EventMapper : Mapper<EventEntity, Event> {

    override fun mapFromEntity(entity: EventEntity): Event {
        val event = Event(entity.date, entity.description, entity.uuid)
        val detailMapper = DetailMapper(event)
        entity.details.forEach {
            event.addDetail(detailMapper.mapFromEntity(it))
        }
        return event
    }

    override fun mapToEntity(model: Event): EventEntity =
        EventEntity(model.uuid, model.date, model.description)

    fun mapFromEntities(type: List<EventEntity>): List<Event> {
        return type.map(this::mapFromEntity)
    }

    fun mapToEntities(type: List<Event>): List<EventEntity> {
        return type.map(this::mapToEntity)
    }
}

