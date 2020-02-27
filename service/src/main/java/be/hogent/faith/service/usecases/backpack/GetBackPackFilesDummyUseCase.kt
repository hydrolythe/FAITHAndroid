package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.FlowableUseCase
import be.hogent.faith.storage.DummyStorageRepository
import io.reactivex.Flowable
import io.reactivex.Scheduler

class GetBackPackFilesDummyUseCase(
    private val dummyStorageRepository: DummyStorageRepository
) {

        fun getDetails(): List<Detail> {
        return dummyStorageRepository.getBackpackDetailsUserTestData()
    }
}