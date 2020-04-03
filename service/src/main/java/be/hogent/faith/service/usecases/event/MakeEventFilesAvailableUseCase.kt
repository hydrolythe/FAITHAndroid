package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 * Download all event files not already in local storage from firebase to local storage
 * Makes offline working possible. Needed for library
 */
class MakeEventFilesAvailableUseCase(
    private val fileStorageRepo: IFileStorageRepository,
    private val eventRepository: IEventRepository,
    private val eventEncryptionService: IEventEncryptionService,
    observeScheduler: Scheduler
) : CompletableUseCase<MakeEventFilesAvailableUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        if (fileStorageRepo.filesReadyToUse(params.event)) {
            return Completable.complete()
        } else {
            return fileStorageRepo.downloadEventFiles(params.event)
                .andThen(
                    eventRepository.get(params.event.uuid)
                        .firstElement()
                        .flatMapCompletable(eventEncryptionService::decryptFiles)
                )
        }
    }

    data class Params(val event: Event)
}