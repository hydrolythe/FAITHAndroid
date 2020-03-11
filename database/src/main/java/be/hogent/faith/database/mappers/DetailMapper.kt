package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.VideoDetail
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
        return when (entity.type) {
            DetailType.AUDIO -> AudioDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            DetailType.TEXT -> TextDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            DetailType.DRAWING -> DrawingDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            DetailType.PHOTO -> PhotoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            DetailType.VIDEO -> VideoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }

    override fun mapToEntity(model: Detail): DetailEntity {
        return DetailEntity(
            FileConverter().toString(model.file), model.fileName, model.uuid.toString(), when (model) {
                is AudioDetail -> DetailType.AUDIO
                is TextDetail -> DetailType.TEXT
                is DrawingDetail -> DetailType.DRAWING
                is PhotoDetail -> DetailType.PHOTO
                is VideoDetail -> DetailType.VIDEO
                else -> throw ClassCastException("Unknown Detail subclass encountered")
            }
        )
    }
}
