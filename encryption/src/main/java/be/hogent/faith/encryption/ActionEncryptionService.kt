package be.hogent.faith.encryption

import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.ActionStatus
import be.hogent.faith.encryption.internal.DataEncrypter
import be.hogent.faith.service.encryption.EncryptedAction
import com.google.crypto.tink.KeysetHandle
import io.reactivex.Single
import timber.log.Timber

class ActionEncryptionService {

    /**
     * Returns an encrypted version of the [action]
     */
    fun encrypt(action: Action, dek: KeysetHandle): Single<EncryptedAction> {
        val dataEncrypter = DataEncrypter(dek)
        return Single.just(
            EncryptedAction(
                description = dataEncrypter.encrypt(action.description),
                currentStatus = dataEncrypter.encrypt(action.currentStatus.name)
            )
        )
            .doOnSuccess { Timber.i("Encrypted detail data for action ${action.description}") }
    }

    /**
     * Decrypts the data of the [encryptedAction], resulting in a regular [Action].
     */
    fun decrypt(
        encryptedAction: EncryptedAction,
        dek: KeysetHandle
    ): Single<Action> {
        val dataEncrypter = DataEncrypter(dek)
        return Single.just(
            Action(
                description = dataEncrypter.decrypt(encryptedAction.description),
                currentStatus = ActionStatus.valueOf(dataEncrypter.decrypt(encryptedAction.currentStatus))
            )
        )
            .doOnSuccess { Timber.i("Decrypted action data for action ${encryptedAction.description}") }
    }
}
