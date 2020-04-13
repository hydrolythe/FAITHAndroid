package be.hogent.faith.database.common

import be.hogent.faith.database.Mapper
import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.service.encryption.EncryptedDetail
import java.util.UUID

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */

object DetailMapper : Mapper<EncryptedDetailEntity, EncryptedDetail> {

    /**
     * Temporarily added for backpack compatibility
     */
    fun mapToEntity(model: Detail): DetailEntity {
        return DetailEntity(
            FileConverter().toString(model.file), model.uuid.toString(),
            model.title,
            when (model) {
                is AudioDetail -> DetailType.AUDIO
                is TextDetail -> DetailType.TEXT
                is DrawingDetail -> DetailType.DRAWING
                is PhotoDetail -> DetailType.PHOTO
                else -> throw ClassCastException("Unknown Detail subclass encountered")
            }
        )
    }

    fun mapFromEntity(entity: DetailEntity): Detail {
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
            DetailType.EXTERNAL_VIDEO -> ExternalVideoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            DetailType.EXTERNAL_VIDEO -> ExternalVideoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            null -> throw IllegalArgumentException("DetailEntity had null type when trying to map")
        }
    }

    override fun mapToEntity(model: EncryptedDetail): EncryptedDetailEntity {
        return EncryptedDetailEntity(
            file = model.file.path,
            title = model.title,
            uuid = model.uuid.toString(),
            type = model.type
        )
    }

    override fun mapToEntity(model: Detail): DetailEntity {
        return DetailEntity(
            FileConverter().toString(model.file),
            model.fileName,
            model.uuid.toString(),
            when (model) {
                is AudioDetail -> DetailType.AUDIO
                is TextDetail -> DetailType.TEXT
                is DrawingDetail -> DetailType.DRAWING
                is PhotoDetail -> DetailType.PHOTO
                is VideoDetail -> DetailType.VIDEO
                is ExternalVideoDetail -> DetailType.EXTERNAL_VIDEO
                else -> throw ClassCastException("Unknown Detail subclass encountered")
            }
        )
    }

    override fun mapFromEntities(entities: List<EncryptedDetailEntity>): List<EncryptedDetail> {
        return entities.map(DetailMapper::mapFromEntity)
    }
}
