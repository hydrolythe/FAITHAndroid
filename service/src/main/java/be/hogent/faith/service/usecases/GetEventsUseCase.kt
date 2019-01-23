package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.Repository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import java.util.concurrent.Executor

class GetEventsUseCase(
    private val eventRepository: Repository<Event>,
    threadExecutor: Executor,
    scheduler: Scheduler
) : FlowableUseCase<List<Event>, Void?>(threadExecutor, scheduler) {
    override fun buildUseCaseObservable(params: Void?): Flowable<List<Event>> {
        return eventRepository.getAll()
    }
}