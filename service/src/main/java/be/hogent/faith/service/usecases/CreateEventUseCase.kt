package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.Repository
import be.hogent.faith.service.usecases.base.CompleteableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDateTime
import java.util.concurrent.Executor

open class CreateEventUseCase(
    private val dateTime: LocalDateTime,
    private val description: String,
    private val eventRepository: Repository<Event>,
    threadExecutor: Executor,
    scheduler: Scheduler
) : CompleteableUseCase<CreateEventUseCase.CreateEventParameters>(threadExecutor, scheduler) {

    override fun buildUseCaseObservable(params : CreateEventUseCase.CreateEventParameters?): Completable {
        val event = Event(params!!.dateTime, params.description)
        return eventRepository.insert(event)
    }

    class CreateEventParameters(
        val dateTime: LocalDateTime,
        val description: String
    )
}