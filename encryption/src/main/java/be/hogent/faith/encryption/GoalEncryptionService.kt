package be.hogent.faith.encryption

import be.hogent.faith.domain.models.goals.ColorGoals
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.ReachGoalWay
import be.hogent.faith.domain.models.goals.SubGoal
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.service.encryption.EncryptedSubGoal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class GoalEncryptionService(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter,
    private val subgoalEncryptionService: SubGoalEncryptionService
) : IGoalEncryptionService {

    override fun encrypt(goal: Goal): Single<EncryptedGoal> {
        val dataKey = keyGenerator.generateKeysetHandle()

        val encryptedSubGoals = encryptSubGoals(goal, dataKey)
        return Singles.zip(
            encryptGoalData(goal, dataKey),
            encryptedSubGoals
        ) { encryptedGoal, subgoals ->
            encryptedGoal.subgoals = subgoals
            encryptedGoal
        }
    }

    /**
     * Returns an encrypted version of the [Goal], only the data, not the subgoals.
     */
    private fun encryptGoalData(
        goal: Goal,
        dataKey: KeysetHandle
    ): Single<EncryptedGoal> {
        val encryptedDEK = keyEncrypter.encrypt(dataKey)
            .doOnSuccess { Timber.i("Encrypted dek for ${goal.uuid}") }

        return encryptedDEK
            .flatMap { dek ->
                Single.just(with(DataEncrypter(dataKey)) {
                    EncryptedGoal(
                        dateTime = encrypt(goal.dateTime.toString()),
                        description = encrypt(goal.description),
                        uuid = goal.uuid,
                        isCompleted = goal.isCompleted,
                        currentPositionAvatar = goal.currentPositionAvatar,
                        color = encrypt(goal.color.name),
                        reachGoalWay = encrypt(goal.chosenReachGoalWay.name),
                        encryptedDEK = dek
                    )
                })
            }
    }

    private fun encryptSubGoals(
        goal: Goal,
        dek: KeysetHandle
    ): Single<List<EncryptedSubGoal>> {
        val subgoals: MutableMap<Int, SubGoal> = mutableMapOf<Int, SubGoal>()
        goal.subGoals.forEachIndexed { index, element ->
            if (element != null)
                subgoals.put(index, element)
        }
        return Observable.fromIterable(subgoals.entries)
        .flatMapSingle { subgoalEncryptionService.encrypt(it.component1(), it.component2(), dek) }
            .toList() // todo remove null
        /*
        return Observable.fromArray(goal.subGoals)
            .flatMapSingle { subgoalEncryptionService.encrypt(it, dek) }.toList().map{it.toTypedArray()}
         */
    }

    override fun decryptData(encryptedGoal: EncryptedGoal): Single<Goal> {
        return keyEncrypter
            .decrypt(encryptedGoal.encryptedDEK)
            .doOnSuccess { Timber.i("decrypted dek for ${encryptedGoal.uuid}") }
            .flatMap { dek -> decryptGoalData(encryptedGoal, dek) }
    }

    private fun decryptGoalData(encryptedGoal: EncryptedGoal, dek: KeysetHandle): Single<Goal> {
        return decryptSubGoals(encryptedGoal, dek)
            .map { decryptedSubgoals ->
                with(DataEncrypter(dek)) {
                    val goal = Goal(
                        dateTime = LocalDateTime.parse(decrypt(encryptedGoal.dateTime)),
                        description = encryptedGoal.description.let { decrypt(it) },
                        uuid = encryptedGoal.uuid,
                        isCompleted = encryptedGoal.isCompleted,
                        currentPositionAvatar = encryptedGoal.currentPositionAvatar,
                        color = ColorGoals.valueOf(encryptedGoal.color),
                        chosenReachGoalWay = ReachGoalWay.valueOf(encryptedGoal.reachGoalWay)
                    )
                    goal
                }
            }
    }

    private fun decryptSubGoals(
        encryptedGoal: EncryptedGoal,
        dek: KeysetHandle
    ): Single<List<Pair<SubGoal, Int>>> {
        return Observable.fromIterable(encryptedGoal.subgoals)
            .flatMapSingle { subgoalEncryptionService.decrypt(it, dek) }.toList()
    }

    private fun addGoals(goal: Goal, subgoals: List<Pair<SubGoal, Int>>) {
        subgoals.forEach { goal.addSubGoal(it.first, it.second) }
    }
}