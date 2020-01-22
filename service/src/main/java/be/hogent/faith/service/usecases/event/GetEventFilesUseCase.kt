package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Download all event files not already in local storage from firebase to local storage
 * Makes offline working possible. Needed for library
 */
class GetEventFilesUseCase(
    private val storageRepo: IStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<GetEventFilesUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return storageRepo.downloadEventFiles(params.event)
    }

    data class Params(val event: Event)
}