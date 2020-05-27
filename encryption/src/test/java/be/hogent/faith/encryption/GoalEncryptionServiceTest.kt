package be.hogent.faith.encryption

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.encryption.di.encryptionModule
import be.hogent.faith.encryption.encryptionService.DummyKeyEncryptionService
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.util.factory.GoalFactory
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

class GoalEncryptionServiceTest : KoinTest {
    private val keyGenerator = KeyGenerator()
    private val keyEncrypter = KeyEncrypter(DummyKeyEncryptionService())

    private val goalEncrypter = GoalEncryptionService(
        keyGenerator,
        keyEncrypter
    )

    private val goal =
        GoalFactory.makeGoal()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(encryptionModule)
    }

    @Test
    fun `After encrypting a goal none of the sensitive data is in a human readable format`() {
        // Act
        goalEncrypter.encrypt(goal)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue { encryptedGoal ->
                encryptedGoal.dateTime != goal.dateTime.toString() &&
                        encryptedGoal.description != goal.description &&
                        encryptedGoal.uuid == goal.uuid
            }
            .dispose()
    }

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