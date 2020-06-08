package be.hogent.faith.database.goal

import be.hogent.faith.database.Mapper
import be.hogent.faith.service.encryption.EncryptedAction

/**
 * Maps details of a given action.
 */

object ActionMapper : Mapper<EncryptedActionEntity, EncryptedAction> {
    override fun mapToEntity(model: EncryptedAction): EncryptedActionEntity {
        return EncryptedActionEntity(
            description = model.description,
            currentStatus = model.currentStatus
        )
    }

    override fun mapToEntities(models: List<EncryptedAction>): List<EncryptedActionEntity> {
        return models.map(ActionMapper::mapToEntity)
    }

    override fun mapFromEntity(entity: EncryptedActionEntity): EncryptedAction {
        return EncryptedAction(
            description = entity.description,
            currentStatus = entity.currentStatus
        )
    }

    override fun mapFromEntities(entities: List<EncryptedActionEntity>): List<EncryptedAction> {
        return entities.map(ActionMapper::mapFromEntity)
    }
}
