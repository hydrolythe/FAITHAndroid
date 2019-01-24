package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class GetEventsUseCase(
    private val eventRepository: EventRepository,
    observeScheduler: Scheduler
) : FlowableUseCase<List<Event>, Void?>(Schedulers.io(), observeScheduler) {
    override fun buildUseCaseObservable(params: Void?): Flowable<List<Event>> {
        return eventRepository.getAll()
    }
}