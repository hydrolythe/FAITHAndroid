package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import timber.log.Timber

/**
 * Download all event files not already in storage to local storage, and places a decrypted version
 * in cache storage.
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
        return Completable.defer {
            if (fileStorageRepo.filesReadyToUse(params.event)) {
                Completable.complete()
                    .doOnComplete {
                        Timber.i("Files for ${params.event.uuid} are ready to use")
                    }
            } else {
                fileStorageRepo.downloadEventFiles(params.event)
                    .doOnComplete { Timber.i("Files for ${params.event.uuid} have finished downloading") }
                    .concatWith(
                        eventRepository.get(params.event.uuid)
                            .firstElement()
                            .doOnSubscribe { Timber.i("Starting to decrypt the files for event ${params.event.uuid}") }
                            .flatMapCompletable(eventEncryptionService::decryptFiles)
                            .doOnComplete { Timber.i("Files for event ${params.event.uuid} have been decrypted") }
                    )
            }
        }
    }

    data class Params(val event: Event)
}