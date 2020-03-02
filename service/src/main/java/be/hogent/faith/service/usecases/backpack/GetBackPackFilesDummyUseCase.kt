package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.storage.backpack.DummyStorageRepository

class GetBackPackFilesDummyUseCase(
    private val dummyStorageRepository: DummyStorageRepository
) {

        fun getDetails(): List<Detail> {
        return dummyStorageRepository.getBackpackDetailsUserTestData()
    }
}