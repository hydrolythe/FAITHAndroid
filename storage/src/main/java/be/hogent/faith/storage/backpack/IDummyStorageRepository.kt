package be.hogent.faith.storage.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable
import io.reactivex.Flowable

interface IDummyStorageRepository {

    fun storeDetail(detail: Detail): Completable

    fun getBackpackDetailsUserTestData() : List<Detail>

}