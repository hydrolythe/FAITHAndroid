package be.hogent.faith.encryption

import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedSubGoal
import be.hogent.faith.util.factory.SubGoalFactory
import org.junit.Test

class SubGoalEncryptionServiceTest {
    private val dek by lazy { KeyGenerator().generateKeysetHandle() }

    private val subgoalEncrypter =
        SubGoalEncryptionService(ActionEncryptionService())

    private val subgoal = SubGoalFactory.makeSubGoal(3)

    @Test
    fun `After decrypting an encrypted subgoal its data is back to the original values`() {
        // Arrange
        lateinit var encryptedSubGoal: EncryptedSubGoal
        subgoalEncrypter.encrypt(1, subgoal, dek)
            .doOnSuccess { encryptedSubGoal = it }
            .test()
            .assertComplete()
            .dispose()

        // Act
        subgoalEncrypter.decrypt(encryptedSubGoal, dek)
            .test()
            .assertComplete()
            .assertValue { decryptedSubGoal ->
                subgoal.description == decryptedSubGoal.first.description &&
                subgoal.actions.count() == decryptedSubGoal.first.actions.count()
                1 == decryptedSubGoal.second
            }
            .dispose()
    }
}