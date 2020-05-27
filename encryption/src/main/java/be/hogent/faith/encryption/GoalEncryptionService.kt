package be.hogent.faith.encryption

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedGoal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Single

import org.koin.core.KoinComponent
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class GoalEncryptionService(
    private val keyGenerator: KeyGenerator,
    private val keyEncrypter: KeyEncrypter
) : IGoalEncryptionService, KoinComponent {

    override fun encrypt(goal: Goal): Single<EncryptedGoal> {
        val dataKey = keyGenerator.generateKeysetHandle()
        return encryptGoalData(
            goal,
            dataKey
        )
    }

    override fun decryptData(encryptedGoal: EncryptedGoal): Single<Goal> {
        return keyEncrypter
            .decrypt(encryptedGoal.encryptedDEK)
            .doOnSuccess { Timber.i("decrypted dek for ${encryptedGoal.uuid}") }
            .flatMap { dek -> decryptGoalData(encryptedGoal, dek) }
    }

    private fun decryptGoalData(encryptedGoal: EncryptedGoal, dek: KeysetHandle): Single<Goal> {
        return Single.just(with(DataEncrypter(dek)) {
            Goal(
                dateTime = LocalDateTime.parse(decrypt(encryptedGoal.dateTime)),
                description = encryptedGoal.description.let { decrypt(it) },
                uuid = encryptedGoal.uuid,
                isCompleted = encryptedGoal.isCompleted
            )
        })
    }

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
                        encryptedDEK = dek
                    )
                })
            }
    }
}