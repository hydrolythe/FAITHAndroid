package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.repositories.ITemporaryFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventDetailUseCase(
    private val tempStorageRepo: ITemporaryFileStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        if (params.event.details.contains(params.detail)) {
            return Completable.complete()
        }
        return tempStorageRepo.storeDetailWithEvent(
            params.detail,
            params.event
        ).doOnComplete {
            params.event.addDetail(params.detail)
        }
    }

    data class Params(
        val detail: Detail,
        val event: Event
    )
}
