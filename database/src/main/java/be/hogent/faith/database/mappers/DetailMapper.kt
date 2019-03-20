package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.domain.models.detail.TextDetail
import java.util.UUID

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */
class DetailMapper : MapperWithForeignKey<DetailEntity, Detail> {
    override fun mapFromEntities(entities: List<DetailEntity>): List<Detail> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntities(models: List<Detail>, foreignKey: UUID): List<DetailEntity> {
        return models.map { mapToEntity(it, foreignKey) }
    }

    override fun mapFromEntity(entity: DetailEntity): Detail {
        return when (entity) {
            is AudioDetailEntity -> AudioDetail(entity.file, entity.uuid)
            is TextDetailEntity -> TextDetail(entity.file, entity.uuid)
            is PictureDetailEntity -> PictureDetail(entity.file, entity.uuid)
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }

    override fun mapToEntity(model: Detail, foreignKey: UUID): DetailEntity {
        return when (model) {
            is AudioDetail -> AudioDetailEntity(model.file, model.uuid, foreignKey)
            is TextDetail -> TextDetailEntity(model.file, model.uuid, foreignKey)
            is PictureDetail -> PictureDetailEntity(model.file, model.uuid, foreignKey)
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }
}