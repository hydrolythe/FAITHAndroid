package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventDetailUseCase(
    private val tempStorageRepo: ITemporaryStorage,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        // TODO : Stond erbij voor text en drawing. Is ook ok voor de rest?
        if (params.event.details.contains(params.detail)) {
            return Completable.complete()
        }
        return tempStorageRepo.storeDetailWithContainer(
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