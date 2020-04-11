package be.hogent.faith.encryption.encryptionService

import be.hogent.faith.encryption.internal.DecryptionRequest
import be.hogent.faith.encryption.internal.EncryptionRequest
import be.hogent.faith.encryption.internal.KeyEncryptionService
import be.hogent.faith.util.withSuffix
import io.reactivex.Single

private const val SUFFIX = "_encrypted"

class DummyKeyEncryptionService :
    KeyEncryptionService {
    override fun encrypt(request: EncryptionRequest): Single<String> {
        return Single.just(
            request.plaintext.withSuffix(SUFFIX)
        )
    }

    override fun decrypt(request: DecryptionRequest): Single<String> {
        return Single.just(
            request.ciphertext.removeSuffix(SUFFIX)
        )
    }
}