package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */
//TODO: write tests
class DetailMapper(private val event: Event) : Mapper<DetailEntity, Detail> {
    override fun mapFromEntity(entity: DetailEntity): Detail {
        val type: DetailType = DetailTypeMapper.mapFromEntity(entity.type)
        return Detail(type, entity.uuid)
    }

    override fun mapToEntity(model: Detail): DetailEntity {
        val entityType = DetailTypeMapper.mapToEntity(model.detailType)
        return DetailEntity(model.uuid, event.uuid, entityType)
    }

}