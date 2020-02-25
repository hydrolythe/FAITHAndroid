package be.hogent.faith.storage

import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Flowable

interface IDummyStorageRepository {

    fun storeDetail(detail: Detail): Completable

    fun getBackpackDetailsUserTestData() : List<Detail>

}