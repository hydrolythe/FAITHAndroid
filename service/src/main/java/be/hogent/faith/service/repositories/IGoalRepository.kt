package be.hogent.faith.service.repositories

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
     * Deletes the goal with given [goalUuid] for the currently authenticated user.
     */
    fun delete(goalUuid: UUID): Completable

    /**
     * Returns all goals  associated with the currently authenticated user.
     */
    fun getAll(): Flowable<List<EncryptedGoal>>
}