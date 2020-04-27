package be.hogent.faith.database.common

import be.hogent.faith.database.Mapper
import be.hogent.faith.service.encryption.EncryptedDetail
import java.io.File
import java.util.UUID

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */

object DetailMapper : Mapper<EncryptedDetailEntity, EncryptedDetail> {
    override fun mapToEntity(model: EncryptedDetail): EncryptedDetailEntity {
        return EncryptedDetailEntity(
            file = model.file.path,
            title = model.title,
            uuid = model.uuid.toString(),
            type = model.type,
            dateTime = model.dateTime,
            youtubeVideoId = model.youtubeVideoID
        )
    }

    override fun mapToEntities(models: List<EncryptedDetail>): List<EncryptedDetailEntity> {
        return models.map(DetailMapper::mapToEntity)
    }

    override fun mapFromEntity(entity: EncryptedDetailEntity): EncryptedDetail {
        return EncryptedDetail(
            file = File(entity.file),
            title = entity.title,
            uuid = UUID.fromString(entity.uuid),
            type = entity.type,
            dateTime = entity.dateTime,
            youtubeVideoID = entity.youtubeVideoId
        )
    }

    override fun mapFromEntities(entities: List<EncryptedDetailEntity>): List<EncryptedDetail> {
        return entities.map(DetailMapper::mapFromEntity)
    }
}
