package be.hogent.faith.database.common

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.goal.EncryptedGoalEntity
import be.hogent.faith.database.goal.GoalMapper
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.util.factory.DataFactory
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalMapperTest {

    private val goalMapper = GoalMapper
    private val user = UserFactory.makeUser()

    private val encryptedGoal = EncryptedGoal(
        description = DataFactory.randomString(),
        uuid = DataFactory.randomUUID(),
        dateTime = DataFactory.randomString(),
        isCompleted = DataFactory.randomBoolean(),
        encryptedDEK = "encrypted version of DEK"
    )

    @Test
    fun goalMapper_mapFromEntity() {
        val goalEntity = EntityFactory.makeGoal()
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
        assertEquals(entity.encryptedDEK, model.encryptedDEK)
    }
}
