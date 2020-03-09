package be.hogent.faith.storage.backpack

import be.hogent.faith.domain.models.detail.Detail
import io.reactivex.Completable

interface IDummyStorageRepository {

    fun storeDetail(detail: Detail): Completable

    fun getBackpackDetailsUserTestData(): List<Detail>
}