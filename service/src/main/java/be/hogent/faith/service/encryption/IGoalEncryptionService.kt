package be.hogent.faith.service.encryption

import be.hogent.faith.domain.models.goals.Goal
import io.reactivex.Single

interface IGoalEncryptionService {
    /**
     * Encrypts the [goal]s data.
     *
     * @return an [EncryptedGoal] whose data is encrypted
     */
    fun encrypt(goal: Goal): Single<EncryptedGoal>

    /**
     * Decrypts the [encryptedGoal]s data
     */
    fun decryptData(encryptedGoal: EncryptedGoal): Single<Goal>
}