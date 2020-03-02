package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackTextDetailUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
): CompletableUseCase<SaveBackpackTextDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.storeDetail(params.textDetail)
    }

    data class Params(val textDetail: TextDetail)
}
