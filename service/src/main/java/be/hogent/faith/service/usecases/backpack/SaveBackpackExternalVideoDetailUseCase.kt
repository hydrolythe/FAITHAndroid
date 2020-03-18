package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackExternalVideoDetailUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackExternalVideoDetailUseCase.Params>(
        observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.storeDetail(params.externalVideoDetail)
    }

    data class Params(val externalVideoDetail: ExternalVideoDetail)
}