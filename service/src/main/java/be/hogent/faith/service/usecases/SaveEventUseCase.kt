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

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromCallable {
            // First add in domain so we can do business logic
            // If this fails the event won't get added to the Repo.
            params.user.addEvent(params.event)
            println("Added ${params.event} to ${params.user}")
        }.andThen {
            eventRepository.insert(params.event, params.user)
            println("Added ${params.event} to repo")
        }
    }

    data class Params(
        val event: Event,
        val user: User
    )
}
