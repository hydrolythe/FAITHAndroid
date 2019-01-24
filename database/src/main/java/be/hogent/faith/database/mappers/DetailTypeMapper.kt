package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.domain.models.DetailType

// TODO: write tests
object DetailTypeMapper : Mapper<DetailTypeEntity, DetailType> {
    override fun mapFromEntities(entities: List<DetailTypeEntity>): List<DetailType> {
        return entities.map { it -> mapFromEntity(it) }
    }

    override fun mapToEntities(models: List<DetailType>): List<DetailTypeEntity> {
        return models.map { it -> mapToEntity(it) }
    }

    override fun mapFromEntity(entity: DetailTypeEntity): DetailType =
        DetailType.valueOf(entity.toString())

    override fun mapToEntity(model: DetailType): DetailTypeEntity =
        DetailTypeEntity.valueOf(model.toString())
}