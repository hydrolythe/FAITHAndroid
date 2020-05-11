package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Download all event files not already in storage to local storage, and places a decrypted version
 * in cache storage.
 */
class MakeEventFilesAvailableUseCase(
    private val fileStorageRepo: IFileStorageRepository,
    private val eventRepository: IEventRepository,
    private val eventEncryptionService: IEventEncryptionService,
    observer: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : SingleUseCase<Event, MakeEventFilesAvailableUseCase.Params>(
    observer, subscriber
) {
    override fun buildUseCaseSingle(params: Params): Single<Event> {
        if (fileStorageRepo.filesReadyToUse(params.event)) {
//            return Completable.complete()
            return Single.just(params.event)
//                .doOnComplete {
//                    Timber.i("Files for ${params.event.uuid} are ready to use")
//                }
        } else {
            return fileStorageRepo.downloadEventFiles(params.event)
                .subscribeOn(subscriber)
                .doOnComplete { Timber.i("Files for ${params.event.uuid} have finished downloading") }
                .concatWith(
                    eventRepository.get(params.event.uuid)
                        .subscribeOn(subscriber)
                        .firstElement()
                        .doOnSubscribe { Timber.i("Starting to decrypt the files for event ${params.event.uuid}") }
                        .flatMapCompletable(eventEncryptionService::decryptFiles)
                        .doOnComplete { Timber.i("Files for event ${params.event.uuid} have been decrypted") }
                )
                .toSingle { params.event }
        }
    }

    data class Params(val event: Event)
}