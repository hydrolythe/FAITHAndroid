package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Places all event files in local storage
 */
class GetEventFilesUseCase(
    private val storageRepo: IStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<GetEventFilesUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return storageRepo.getEvent(params.event)
    }

    data class Params(val event: Event)
}