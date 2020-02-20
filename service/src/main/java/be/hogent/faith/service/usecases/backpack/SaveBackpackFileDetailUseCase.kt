package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackFileDetailUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
): CompletableUseCase<SaveBackpackFileDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.storeDetail(params.detail)
    }

    data class Params(val detail: Detail)
}