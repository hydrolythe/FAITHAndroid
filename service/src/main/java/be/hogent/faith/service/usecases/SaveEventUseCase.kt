package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

open class SaveEventUseCase(
    private val eventRepository: EventRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromCallable {
            // First add in domain so we can do business logic
            // If this fails the event won't get added to the Repo.
            params.user.addEvent(params.event)
        }.andThen(
            // Gebruik ( bij andThen ipv { anders problemen.
            // Zie https://stackoverflow.com/questions/45680256/rxjava-completabe-andthen-testing
            eventRepository.insert(params.event, params.user)
        )
    }

    data class Params(
        val event: Event,
        val user: User
    )
}
