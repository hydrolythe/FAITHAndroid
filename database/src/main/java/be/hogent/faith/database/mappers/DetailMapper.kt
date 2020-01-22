package be.hogent.faith.database.mappers

import be.hogent.faith.database.encryption.EncryptedDetail
import be.hogent.faith.database.encryption.EncryptedDetailInterface
import be.hogent.faith.database.models.EncryptedDetailEntity
import java.io.File
import java.util.UUID

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */

internal object DetailMapper : Mapper<EncryptedDetailEntity, EncryptedDetailInterface> {

    override fun mapToEntity(model: EncryptedDetailInterface): EncryptedDetailEntity {
        return EncryptedDetailEntity(
            file = model.file.path,
            uuid = model.uuid.toString(),
            type = model.type
        )
    }

    override fun mapToEntities(models: List<EncryptedDetailInterface>): List<EncryptedDetailEntity> {
        return models.map(DetailMapper::mapToEntity)
    }

    override fun mapFromEntity(entity: EncryptedDetailEntity): EncryptedDetail {
        return EncryptedDetail(
            file = File(entity.file),
            uuid = UUID.fromString(entity.uuid),
            type = entity.type
        )
    }

    override fun mapFromEntities(entities: List<EncryptedDetailEntity>): List<EncryptedDetail> {
        return entities.map(DetailMapper::mapFromEntity)
    }
}
