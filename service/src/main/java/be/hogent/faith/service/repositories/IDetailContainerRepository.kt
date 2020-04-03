package be.hogent.faith.service.repositories

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

interface IDetailContainerRepository<DetailsContainer> {
    fun insertDetail(encryptedDetail: EncryptedDetail, user: User): Maybe<EncryptedDetail>

    fun get(): Flowable<List<Detail>>

    fun deleteDetail(detail: Detail): Completable
}