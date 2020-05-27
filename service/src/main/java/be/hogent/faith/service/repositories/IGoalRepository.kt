package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.encryption.EncryptedGoal
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.UUID

interface IGoalRepository {
    /**
     * Updates the given [encryptedGoal]  for the authenticated user
     */
    fun update(encryptedGoal: EncryptedGoal): Completable

    /**
     * Adds the given [encryptedGoal] to the goals of the authenticated user
     */
    fun insert(encryptedGoal: EncryptedGoal): Completable

    fun get(uuid: UUID): Flowable<EncryptedGoal>

    /**
     * Deletes the  given [goal]. The [user] is required to check that the goal belongs to
     * the currently authenticated user.
     */
    fun delete(goal: Goal, user: User): Completable

    /**
     * Returns the data of all goals (onlyActive = false) or the not completed goals (onlyActive = true) associated with the currently authenticated user.
     */
    fun getAll(onlyActive: Boolean = true): Flowable<List<EncryptedGoal>>
}