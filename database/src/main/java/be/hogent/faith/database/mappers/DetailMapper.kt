package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import java.util.UUID

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */

object DetailMapper : Mapper<DetailEntity, Detail> {
    override fun mapFromEntities(entities: List<DetailEntity>): List<Detail> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntities(models: List<Detail>): List<DetailEntity> {
        return models.map { mapToEntity(it) }
    }

    override fun mapFromEntity(entity: DetailEntity): Detail {
        val file = FileConverter().toFile(entity.file)
        val uuid = UUID.fromString(entity.uuid)
        return when (entity.type) {
            DetailType.AUDIO -> AudioDetail(file, uuid)
            DetailType.TEXT -> TextDetail(file, uuid)
            DetailType.DRAWING -> DrawingDetail(file, uuid)
            DetailType.PHOTO -> PhotoDetail(file, uuid)
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }

    override fun mapToEntity(model: Detail): DetailEntity {
        val mappedFile = FileConverter().toString(model.file)
        val mappedUUID = model.uuid.toString()
        val detailType = when (model) {
            is AudioDetail -> DetailType.AUDIO
            is TextDetail -> DetailType.TEXT
            is DrawingDetail -> DetailType.DRAWING
            is PhotoDetail -> DetailType.PHOTO
        }
        return DetailEntity(mappedFile, mappedUUID, detailType)
    }
}
