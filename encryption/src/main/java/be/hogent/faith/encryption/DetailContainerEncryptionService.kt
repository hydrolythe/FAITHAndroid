package be.hogent.faith.encryption

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.encryption.internal.KeyEncrypter
import be.hogent.faith.encryption.internal.KeyGenerator
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedDetailsContainer
import be.hogent.faith.service.encryption.IDetailContainerEncryptionService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import timber.log.Timber

class DetailContainerEncryptionService<T>(
    private val detailEncryptionService: DetailEncryptionService,
    private val keyEncrypter: KeyEncrypter,
    private val keyGenerator: KeyGenerator
) : IDetailContainerEncryptionService<T> {
    override fun encrypt(
        detail: Detail,
        encryptedDetailsContainer: EncryptedDetailsContainer
    ): Single<EncryptedDetail> {
        val dekSingle = keyEncrypter.decrypt(encryptedDetailsContainer.encryptedDEK)
        val sdekSingle = keyEncrypter.decrypt(encryptedDetailsContainer.encryptedStreamingDEK)

        return dekSingle.flatMap { dek ->
            sdekSingle.flatMap { sdek ->
                detailEncryptionService.encrypt(detail, dek, sdek)
            }
        }
    }

    override fun decryptFile(detail: Detail, container: EncryptedDetailsContainer): Completable {
        return keyEncrypter.decrypt(container.encryptedStreamingDEK)
            .flatMapCompletable { sdek -> detailEncryptionService.decryptDetailFile(detail, sdek) }.doOnError { Timber.e("Decrypt failed") }
    }

    override fun decryptData(
        encryptedDetail: EncryptedDetail,
        container: EncryptedDetailsContainer
    ): Single<Detail> {
        val dekSingle = keyEncrypter.decrypt(container.encryptedDEK)

        return dekSingle.flatMap { dek ->
            detailEncryptionService.decryptData(encryptedDetail, dek)
        }
    }

    override fun createContainer(): Single<EncryptedDetailsContainer> {
        val encryptedDEK = keyEncrypter.encrypt(keyGenerator.generateKeysetHandle())
            .doOnSuccess { Timber.i("Created dek for container") }
        val encryptedSDEK = keyEncrypter.encrypt(keyGenerator.generateStreamingKeysetHandle())
            .doOnSuccess { Timber.i("Created sdek for container") }

        return Singles.zip(encryptedDEK, encryptedSDEK) { dek, sdek ->
            EncryptedDetailsContainer(dek, sdek)
        }
    }
}