package be.hogent.faith.encryption

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.util.factory.GoalFactory
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalEncryptionServiceTest {
    private val keyGenerator = KeyGenerator()
    private val keyEncrypter = KeyEncrypter(DummyKeyEncryptionService())

    private val goalEncrypter = GoalEncryptionService(
        keyGenerator,
        keyEncrypter,
        SubGoalEncryptionService(ActionEncryptionService())
    )

    private val goal =
        GoalFactory.makeGoal()

    @Test()
    fun `After decrypting an encrypted goal all its data is back to the original values`() {
        // Arrange
        var encryptedGoal: EncryptedGoal? = null
        goalEncrypter.encrypt(goal)
            .doOnSuccess { encryptedGoal = it }
            .test()
            .dispose()

        // Act
        var decryptedGoal: Goal? = null
        goalEncrypter.decryptData(encryptedGoal!!)
            .subscribeOn(Schedulers.trampoline())
            .doOnSuccess { decryptedGoal = it }
            .test()
            .assertNoErrors()
            .dispose()

        // Assert
        assertNotNull(decryptedGoal)
        assertEquals(decryptedGoal!!.dateTime, goal.dateTime)
        assertEquals(decryptedGoal!!.description, goal.description)
        assertEquals(decryptedGoal!!.uuid, goal.uuid)
        assertEquals(decryptedGoal!!.isCompleted, goal.isCompleted)
        assertEquals(decryptedGoal!!.currentPositionAvatar, goal.currentPositionAvatar)
        assertEquals(decryptedGoal!!.color, goal.color)
        assertEquals(decryptedGoal!!.chosenReachGoalWay, goal.chosenReachGoalWay)
        assertEquals(decryptedGoal!!.subGoals.count(), goal.subGoals.count())
    }

    @Test
    fun `goal has a DEK associated with it after encrypting`() {
        // Act
        // Arrange
        var encryptedGoal: EncryptedGoal? = null
        goalEncrypter.encrypt(goal)
            .doOnSuccess { encryptedGoal = it }
            .test()
            .dispose()

        // Assert
        assertTrue(encryptedGoal!!.encryptedDEK.isNotEmpty())
    }
}