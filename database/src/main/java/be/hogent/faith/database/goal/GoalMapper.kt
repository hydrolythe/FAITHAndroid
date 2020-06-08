package be.hogent.faith.database.goal

import be.hogent.faith.database.Mapper
import be.hogent.faith.service.encryption.EncryptedGoal
import java.util.UUID

internal object GoalMapper :
    Mapper<EncryptedGoalEntity, EncryptedGoal> {

    override fun mapFromEntity(entity: EncryptedGoalEntity): EncryptedGoal {
        return EncryptedGoal(
            dateTime = entity.dateTime,
            description = entity.description,
            uuid = UUID.fromString(entity.uuid),
            isCompleted = entity.isCompleted,
            currentPositionAvatar = entity.currentPositionAvatar,
            goalColor = entity.color,
            reachGoalWay = entity.reachGoalWay,
            subgoals = SubGoalMapper.mapFromEntities(
                entity.subgoals
            ),
            encryptedDEK = entity.encryptedDEK
        )
    }

    override fun mapToEntity(model: EncryptedGoal): EncryptedGoalEntity {
        return EncryptedGoalEntity(
            dateTime = model.dateTime,
            description = model.description,
            uuid = model.uuid.toString(),
            isCompleted = model.isCompleted,
            currentPositionAvatar = model.currentPositionAvatar,
            color = model.goalColor,
            reachGoalWay = model.reachGoalWay,
            subgoals = SubGoalMapper.mapToEntities(
                model.subgoals.toList()
            ),
            encryptedDEK = model.encryptedDEK
        )
    }

    override fun mapToEntities(models: List<EncryptedGoal>): List<EncryptedGoalEntity> {
        return models.map(::mapToEntity)
    }

    override fun mapFromEntities(entities: List<EncryptedGoalEntity>): List<EncryptedGoal> {
        return entities.map(::mapFromEntity)
    }
}
