package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

/**
 * Returns all the events associated with a user. The files belonging to  these events are not guaranteed
 * to be available (on storage and decrypted). To make sure the files are available, use the [MakeEventFilesAvailableUseCase].
 */
class GetEventsUseCase(
    private val eventRepository: IEventRepository,
    private val eventEncryptionService: IEventEncryptionService,
    observer: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : FlowableUseCase<List<Event>, GetEventsUseCase.Params>(observer, subscriber) {

    override fun buildUseCaseObservable(params: Params): Flowable<List<Event>> {
        return eventRepository.getAll()
            .doOnSubscribe { Timber.i("Started listening for new events for user ${params.user.username}") }
            .doOnNext { Timber.i("Downloaded new encrypted events for user ${params.user.username}") }
            .concatMapSingle { list ->
                Observable.fromIterable(list)
                    .flatMapSingle { encryptedEvent ->
                        eventEncryptionService.decryptData(encryptedEvent)
                            .subscribeOn(subscriber)
                            .doOnSuccess { Timber.i("Decrypted data for event ${encryptedEvent.uuid}") }
                    }
                    .toList()
                    .doOnSuccess { Timber.i("Decrypted all events for user ${params.user.username}") }
            }
    }

    data class Params(
        val user: User
    )
}