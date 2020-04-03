package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.usecases.base.ObservableUseCase
import be.hogent.faith.service.repositories.IEventRepository
import io.reactivex.Observable
import io.reactivex.Scheduler

/**
 * Returns all the events associated with a user. The files belonging to  these events are not guaranteed
 * to be available (on storage and decrypted). To make sure the files are available, use the [MakeEventFilesAvailableUseCase].
 */
class GetEventsUseCase(
    private val eventRepository: IEventRepository,
    observeScheduler: Scheduler
) : ObservableUseCase<List<Event>, GetEventsUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Observable<List<Event>> {
        return eventRepository.getAllEventsData()
    }

    data class Params(
        val user: User
    )
}