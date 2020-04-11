package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedDetailsContainer
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface IDetailContainerRepository {
    fun insertDetail(encryptedDetail: EncryptedDetail, user: User): Completable

    fun getEncryptedContainer(): Single<EncryptedDetailsContainer>

    fun get(): Flowable<List<EncryptedDetail>>

    fun deleteDetail(detail: Detail): Completable
}