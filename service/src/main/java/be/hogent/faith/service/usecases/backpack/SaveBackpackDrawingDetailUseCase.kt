package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackDrawingDetailUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
): CompletableUseCase<SaveBackpackDrawingDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.storeDetail(params.drawingDetail)
    }

    data class Params(val drawingDetail: DrawingDetail)
}