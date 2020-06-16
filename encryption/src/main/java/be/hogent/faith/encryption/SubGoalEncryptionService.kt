package be.hogent.faith.encryption

import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.SubGoal
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.service.encryption.EncryptedAction
import be.hogent.faith.service.encryption.EncryptedSubGoal
import com.google.crypto.tink.KeysetHandle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxkotlin.Singles
import timber.log.Timber

class SubGoalEncryptionService(private val actionEncryptionService: ActionEncryptionService) {

    /**
     * Returns an encrypted version of the [SubGoal], both its data and its actions.
     */
    fun encrypt(index: Int, subgoal: SubGoal, dek: KeysetHandle): Single<EncryptedSubGoal> {

        val encryptedActions = encryptActions(subgoal, dek)
        return Singles.zip(
            encryptData(index, subgoal, dek),
            encryptedActions
        ) { encryptedSubgoal, actions ->
            encryptedSubgoal.actions = actions
            encryptedSubgoal
        }
    }

    /**
     * Encrypts the data of  the subgoal.
     * Does not set the [EncryptedSubGoal.actions] to an en encrypted version of the actions!
     * This should be done afterwards, once the actions have been encrypted.
     */
    private fun encryptData(
        index: Int,
        subgoal: SubGoal,
        dek: KeysetHandle
    ): Single<EncryptedSubGoal> {
        val dataEncrypter = DataEncrypter(dek)
        return Single.just(
            EncryptedSubGoal(
                sequenceNumber = index,
                description = dataEncrypter.encrypt(subgoal.description)
            )
        )
            .doOnSuccess { Timber.i("Encrypted detail data for subgoal ${subgoal.description}") }
    }

    /**
     * Encrypts the actions of the [subGoal]
     */
    private fun encryptActions(
        subGoal: SubGoal,
        dek: KeysetHandle
    ): Single<List<EncryptedAction>> {
        return Observable
            .fromIterable(subGoal.actions)
            .flatMapSingle { actionEncryptionService.encrypt(it, dek) }
            .toList()
    }

    /**
     * Returns an decrypted version of the [EncryptedSubGoal], both its data and its actions.
     */
    fun decrypt(encryptedSubGoal: EncryptedSubGoal, dek: KeysetHandle): Single<Pair<SubGoal, Int>> {
        // decrypt teh actions
        val decryptedActions = decryptActions(encryptedSubGoal, dek)
        // decrypt the subgoal and add the decrypted actions
        return Singles.zip(
            decryptData(encryptedSubGoal, dek),
            decryptedActions
        ) { subgoal, actions ->
            actions.forEach { subgoal.first.addAction(it) }
            subgoal
        }
    }

    /**
     * Decrypts the data of the [encryptedSubGoal], resulting in a regular [SubGoal].
     * The actions of the [encryptedSubGoal] will **not** be decrypted, they wil be decrypted afterwards
     */
    fun decryptData(
        encryptedSubGoal: EncryptedSubGoal,
        dek: KeysetHandle
    ): Single<Pair<SubGoal, Int>> {
        val dataEncrypter = DataEncrypter(dek)
        return Single.just(
            Pair(
                SubGoal(description = dataEncrypter.decrypt(encryptedSubGoal.description)),
                encryptedSubGoal.sequenceNumber
            )
        )
            .doOnSuccess { Timber.i("Decrypted subgoal data for subgoal ${encryptedSubGoal.description}") }
    }

    /**
     * Decrypts the actions in a [encryptedSubGoal].
     */
    fun decryptActions(
        encryptedSubGoal: EncryptedSubGoal,
        dek: KeysetHandle
    ): Single<List<Action>> {
        return Observable
            .fromIterable(encryptedSubGoal.actions)
            .flatMapSingle { actionEncryptionService.decrypt(it, dek) }
            .toList()
    }
}
