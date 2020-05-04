package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler

/**
 * Returns all the events associated with a user. The files belonging to  these events are not guaranteed
 * to be available (on storage and decrypted). To make sure the files are available, use the [MakeEventFilesAvailableUseCase].
 */
class GetEventsUseCase(
    private val eventRepository: IEventRepository,
    private val eventEncryptionService: IEventEncryptionService,
    observeScheduler: Scheduler
) : FlowableUseCase<List<Event>, GetEventsUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Flowable<List<Event>> {
        return eventRepository.getAll()
            .concatMapSingle { list ->
                Observable.fromIterable(list)
                    .flatMapSingle(eventEncryptionService::decryptData)
                    .toList()
            }
    }

    data class Params(
        val user: User
    )
}