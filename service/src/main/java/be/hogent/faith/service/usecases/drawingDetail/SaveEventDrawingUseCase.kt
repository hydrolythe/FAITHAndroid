package be.hogent.faith.service.usecases.drawingDetail

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventDrawingUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventDrawingUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        if (params.event.details.contains(params.detail)) {
            // The detail's file should contain the latest bitmap so no changes are needed
            return Completable.complete()
        } else {
            return storageRepository.storeDrawingDetailWithEvent(params.detail, params.event)
                .doOnComplete { params.event.addDetail(params.detail) }
        }
    }

    data class Params(
        val detail: DrawingDetail,
        val event: Event
    )
}