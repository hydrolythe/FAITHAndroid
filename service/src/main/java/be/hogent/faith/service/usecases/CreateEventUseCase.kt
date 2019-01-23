package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.Repository
import be.hogent.faith.service.usecases.base.CompleteableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.threeten.bp.LocalDateTime
import java.util.concurrent.Executor

open class CreateEventUseCase(
    dateTime: LocalDateTime,
    description: String,
    private val eventRepository: Repository<Event>,
    threadExecutor: Executor,
    scheduler: Scheduler
) : CompleteableUseCase(threadExecutor, scheduler) {
    override fun buildUseCaseObservable(): Completable {
        eventRepository.add(Event(dateTime, description))
    }
}