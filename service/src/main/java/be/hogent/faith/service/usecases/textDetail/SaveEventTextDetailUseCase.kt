package be.hogent.faith.service.usecases.textDetail

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventTextDetailUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventTextDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        if (params.event.details.contains(params.detail)) {
            return Completable.complete()
        } else {
            return storageRepository.storeTextDetailWithEvent(params.detail, params.event)
                .doOnComplete { params.event.addDetail(params.detail) }
        }
    }

    data class Params(
        val detail: TextDetail,
        val event: Event
    )
}