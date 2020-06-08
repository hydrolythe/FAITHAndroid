package be.hogent.faith.database.common

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.goal.EncryptedActionEntity
import be.hogent.faith.database.goal.EncryptedGoalEntity
import be.hogent.faith.database.goal.EncryptedSubGoalEntity
import be.hogent.faith.database.goal.GoalMapper
import be.hogent.faith.service.encryption.EncryptedAction
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.service.encryption.EncryptedSubGoal
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalMapperTest {

    private val goalMapper = GoalMapper
    private val user = UserFactory.makeUser()

    private val encryptedAction = EncryptedAction(
        description = DataFactory.randomString(),
        currentStatus = DataFactory.randomString()
    )

    private val encryptedSubGoal = EncryptedSubGoal(
        sequenceNumber = DataFactory.randomInt(0, 9),
        description = DataFactory.randomString(),
        actions = listOf(encryptedAction)
    )

    private val encryptedGoal = EncryptedGoal(
        description = DataFactory.randomString(),
        uuid = DataFactory.randomUUID(),
        dateTime = DataFactory.randomString(),
        isCompleted = DataFactory.randomBoolean(),
        currentPositionAvatar = DataFactory.randomInt(1, 10),
        goalColor = DataFactory.randomString(),
        subgoals = listOf(encryptedSubGoal),
        reachGoalWay = DataFactory.randomString(),
        encryptedDEK = "encrypted version of DEK"
    )

    @Test
    fun goalMapper_mapFromEntity() {
        val goalEntity = EntityFactory.makeGoalEntity(DataFactory.randomUUID())
        val resultingGoal = goalMapper.mapFromEntity(goalEntity)
        assertEqualData(goalEntity, resultingGoal)
    }

    @Test
    fun goalMapper_mapToEntity() {
        val resultingGoalEntity = goalMapper.mapToEntity(encryptedGoal)
        assertEqualData(resultingGoalEntity, encryptedGoal)
    }

    private fun assertEqualData(
        entity: EncryptedGoalEntity,
        model: EncryptedGoal
    ) {
        assertEquals(entity.uuid, model.uuid.toString())
        assertEquals(entity.dateTime, model.dateTime)
        assertEquals(entity.description, model.description)
        assertEquals(entity.isCompleted, model.isCompleted)
        assertEquals(entity.currentPositionAvatar, model.currentPositionAvatar)
        assertEquals(entity.color, model.goalColor)
        assertEquals(entity.reachGoalWay, model.reachGoalWay)
        assertEquals(entity.encryptedDEK, model.encryptedDEK)
        for ((index, value) in entity.subgoals.withIndex())
            assertEqualSubGoalData(value, model.subgoals[index])
    }

    private fun assertEqualSubGoalData(
        entity: EncryptedSubGoalEntity,
        model: EncryptedSubGoal
    ) {
        assertEquals(entity.sequenceNumber, model.sequenceNumber)
        assertEquals(entity.description, model.description)
        for ((index, value) in entity.actions.withIndex())
            assertEqualActionData(value, model.actions[index])
    }

    private fun assertEqualActionData(
        entity: EncryptedActionEntity,
        model: EncryptedAction
    ) {
        assertEquals(entity.description, model.description)
        assertEquals(entity.currentStatus, model.currentStatus)
    }
}
