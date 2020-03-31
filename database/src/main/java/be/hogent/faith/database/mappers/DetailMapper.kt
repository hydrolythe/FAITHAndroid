package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.database.models.EncryptedDetailEntity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import java.io.File
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
            model.fileName,
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
            DetailType.VIDEO -> VideoDetail(
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
            uuid = model.uuid.toString(),
            type = model.type
        )
    }

    override fun mapToEntities(models: List<EncryptedDetail>): List<EncryptedDetailEntity> {
        return models.map(DetailMapper::mapToEntity)
    }

    override fun mapFromEntity(entity: EncryptedDetailEntity): EncryptedDetail {
        return EncryptedDetail(
            file = File(entity.file),
            fileName = entity.fileName,
            uuid = UUID.fromString(entity.uuid),
            type = entity.type
        )
    }

    fun mapList(entities: List<DetailEntity>): List<Detail> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapFromEntities(entities: List<EncryptedDetailEntity>): List<EncryptedDetail> {
        return entities.map(DetailMapper::mapFromEntity)
    }
}
