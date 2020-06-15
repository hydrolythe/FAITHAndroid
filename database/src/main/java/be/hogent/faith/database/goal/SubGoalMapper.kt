package be.hogent.faith.database.goal

import be.hogent.faith.database.Mapper
import be.hogent.faith.service.encryption.EncryptedSubGoal

/**
 * Maps details of a given subgoal.
 */

object SubGoalMapper : Mapper<EncryptedSubGoalEntity, EncryptedSubGoal> {
    override fun mapToEntity(model: EncryptedSubGoal): EncryptedSubGoalEntity {
        return EncryptedSubGoalEntity(
            sequenceNumber = model.sequenceNumber,
            description = model.description,
            actions = ActionMapper.mapToEntities(
                model.actions
            )
        )
    }

    override fun mapToEntities(models: List<EncryptedSubGoal>): List<EncryptedSubGoalEntity> {
        return models.map(SubGoalMapper::mapToEntity)
    }

    override fun mapFromEntity(entity: EncryptedSubGoalEntity): EncryptedSubGoal {
        return EncryptedSubGoal(
            sequenceNumber = entity.sequenceNumber,
            description = entity.description,
            actions = ActionMapper.mapFromEntities(
                entity.actions
            )
        )
    }

    override fun mapFromEntities(entities: List<EncryptedSubGoalEntity>): List<EncryptedSubGoal> {
        return entities.map(SubGoalMapper::mapFromEntity)
    }
}
