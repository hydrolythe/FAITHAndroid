package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.domain.models.DetailType

//TODO: write tests
object DetailTypeMapper : Mapper<DetailTypeEntity, DetailType> {
    override fun mapFromEntity(entity: DetailTypeEntity): DetailType =
        DetailType.valueOf(entity.toString())


    override fun mapToEntity(model: DetailType): DetailTypeEntity =
        DetailTypeEntity.valueOf(model.toString())

}