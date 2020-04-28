package be.hogent.faith.service.encryption

import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Single

interface IDetailContainerEncryptionService<T> {
    /**
     * Encrypts a detail belonging (or that will belong) to the given [encryptedDetailsContainer].
     * The [encryptedDetailsContainer] is passed because it holds encrypted versions of the DEK
     * and SDEK that are required to encrypt the detail.
     */
    fun encrypt(
        detail: Detail,
        encryptedDetailsContainer: EncryptedDetailsContainer
    ): Single<EncryptedDetail>

    /**
     * Decrypts the [details]s data, but not its files.
     */
    fun decryptData(
        encryptedDetail: EncryptedDetail,
        container: EncryptedDetailsContainer
    ): Single<Detail>

    fun decryptFile(detail: Detail, container: EncryptedDetailsContainer): Completable

    fun createContainer(): Single<EncryptedDetailsContainer>
}
