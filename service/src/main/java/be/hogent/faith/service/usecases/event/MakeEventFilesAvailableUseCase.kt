package be.hogent.faith.service.usecases.event

import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Download all event files not already in local storage from firebase to local storage
 * Makes offline working possible. Needed for library
 */
class MakeEventFilesAvailableUseCase(
    private val fileStorageRepo: IFileStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<MakeEventFilesAvailableUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return fileStorageRepo.downloadEventFiles(params.event)
    }

    data class Params(val event: Event)
}