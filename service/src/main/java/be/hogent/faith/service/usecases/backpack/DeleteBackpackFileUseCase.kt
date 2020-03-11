package be.hogent.faith.service.usecases.backpack


import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class DeleteBackpackFileUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<DeleteBackpackFileUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.deleteDetail(params.detail)
    }

    data class Params(val detail: Detail)
}
