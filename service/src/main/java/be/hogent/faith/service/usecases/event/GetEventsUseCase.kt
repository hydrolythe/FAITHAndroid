package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler

class GetEventsUseCase(
    private val eventRepository: EventRepository,
    observeScheduler: Scheduler
) : FlowableUseCase<List<Event>, GetEventsUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Flowable<List<Event>> {
        return eventRepository.getAll(params.user)
    }

    data class Params(
        val user: User
    )
}