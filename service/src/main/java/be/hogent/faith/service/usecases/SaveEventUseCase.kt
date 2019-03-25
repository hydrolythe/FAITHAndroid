package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

open class SaveEventUseCase(
    private val eventRepository: EventRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(Schedulers.io(), observeScheduler) {

    override fun buildUseCaseObservable(params: SaveEventUseCase.Params): Completable {
        return eventRepository.insert(params.event, params.user)
    }

    data class Params(
        val event: Event,
        val user: User
    )
}
