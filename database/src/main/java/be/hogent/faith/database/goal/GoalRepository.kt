package be.hogent.faith.database.goal

import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.service.repositories.IGoalRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.util.UUID

class GoalRepository(
    private val goalDatabase: GoalDatabase
) : IGoalRepository {

    private val goalMapper = GoalMapper

    /**
     * Gets the goal with the given [uuid] for the currently authenticated user.
     */
    override fun get(uuid: UUID): Flowable<EncryptedGoal> {
        return goalDatabase.get(uuid)
            .map(goalMapper::mapFromEntity)
    }

    /**
     * updates the goal for the currently authenticated user.
     */
    override fun update(encryptedGoal: EncryptedGoal): Completable {
        return Single.just(encryptedGoal)
            .map(goalMapper::mapToEntity)
            .flatMapCompletable(goalDatabase::update)
    }

    /**
     * Adds the goal for the authenticated user
     */
    override fun insert(encryptedGoal: EncryptedGoal): Completable {
        return Single.just(encryptedGoal)
            .map(goalMapper::mapToEntity)
            .flatMapCompletable(goalDatabase::insert)
    }

    /**
     * Deletes the goal with [goalUuid] for the currently authenticated user.
     */
    override fun delete(goalUuid: UUID): Completable {
        return goalDatabase.delete(goalUuid)
    }

    /**
     * Get all goals for the currently authenticated user.
     */
    override fun getAll(): Flowable<List<EncryptedGoal>> {
        return goalDatabase.getAll()
            .map(goalMapper::mapFromEntities)
    }
}
