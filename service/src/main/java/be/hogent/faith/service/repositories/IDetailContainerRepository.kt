package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedDetailsContainer
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface IDetailContainerRepository<T> {
    fun insertDetail(encryptedDetail: EncryptedDetail, user: User): Completable

    fun getEncryptedContainer(): Single<EncryptedDetailsContainer>

    fun saveEncryptedContainer(encryptedDetailsContainer: EncryptedDetailsContainer): Completable

    fun getAll(): Flowable<List<EncryptedDetail>>

    fun deleteDetail(detail: Detail): Completable
}