package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import java.util.UUID

class EventWithDetailsMapper : MapperWithForeignKey<EventWithDetails, Event> {

    override fun mapFromEntity(entity: EventWithDetails): Event {
        val event = EventMapper.mapFromEntity(entity.eventEntity)
        DetailMapper.mapFromEntities(entity.audioDetailEntities).forEach { event.addNewAudioDetail(it.file, it.name!!) }
        DetailMapper.mapFromEntities(entity.textDetailEntities).forEach { event.addNewTextDetail(it.file, it.name!!) }
        DetailMapper.mapFromEntities(entity.pictureDetailEntities)
            .forEach { event.addNewPictureDetail(it.file, it.name!!) }
        return event
    }

    override fun mapToEntity(model: Event, foreignKey: UUID): EventWithDetails {
        return EventWithDetails().also {
            it.eventEntity = EventMapper.mapToEntity(model, foreignKey)
            val mappedEntities = DetailMapper.mapToEntities(model.details, it.eventEntity.uuid)
            it.textDetailEntities =
                mappedEntities.filter { detailEntity ->
                    detailEntity is TextDetailEntity
                } as MutableList<TextDetailEntity>
            it.pictureDetailEntities =
                mappedEntities.filter { detailEntity ->
                    detailEntity is PictureDetailEntity
                } as MutableList<PictureDetailEntity>
            it.audioDetailEntities =
                mappedEntities.filter { detailEntity ->
                    detailEntity is AudioDetailEntity
                } as MutableList<AudioDetailEntity>
        }
    }

    override fun mapFromEntities(entities: List<EventWithDetails>): List<Event> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Event>, foreignKey: UUID): List<EventWithDetails> {
        return models.map { mapToEntity(it, foreignKey) }
    }
}