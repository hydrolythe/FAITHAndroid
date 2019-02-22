package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime

open class CreateEventUseCase(
    private val eventRepository: EventRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<CreateEventUseCase.Params>(Schedulers.io(), observeScheduler) {

    override fun buildUseCaseObservable(params: CreateEventUseCase.Params): Completable {
        val event = Event(params.dateTime, params.title)
        return eventRepository.insert(event)
    }

    class Params(
        val dateTime: LocalDateTime,
        val title: String
    )
}