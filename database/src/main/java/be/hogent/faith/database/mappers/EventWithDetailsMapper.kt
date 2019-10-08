package be.hogent.faith.database.mappers
/*
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import java.util.UUID

class EventWithDetailsMapper : MapperWithForeignKey<EventWithDetails, Event> {

    override fun mapFromEntity(entity: EventWithDetails): Event {
        val event = EventMapper.mapFromEntity(entity.eventEntity)
        DetailMapper.mapFromEntities(entity.detailEntities).forEach { event.addDetail(it) }
        return event
    }

    override fun mapToEntity(model: Event, foreignKey: UUID): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = EventMapper.mapToEntity(model, foreignKey)
            val mappedEntities = DetailMapper.mapToEntities(model.details, it.eventEntity.uuid)
            it.textDetailEntities =
                mappedEntities.filterIsInstance<TextDetailEntity>().toMutableList()
            it.pictureDetailEntities =
                mappedEntities.filterIsInstance<PictureDetailEntity>().toMutableList()
            it.audioDetailEntities =
                mappedEntities.filterIsInstance<AudioDetailEntity>().toMutableList()
        }
    }

    override fun mapFromEntities(entities: List<EventWithDetails>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>, foreignKey: UUID): List<EventWithDetails> {
        return models.map { mapToEntity(it, foreignKey) }
    }
}

 */