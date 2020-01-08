package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single

open class SaveEventUseCase(
    private val eventRepository: EventRepository,
    private val storageRepository: IStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observeScheduler) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return addEventToUser(params.event)
            .flatMap { storageRepository.saveEvent(it) }
            .flatMapMaybe { eventRepository.insert(it, params.user) }
            .flatMapCompletable { Completable.complete()
            }
    }

    private fun addEventToUser(event: Event): Single<Event> = Single.fromCallable {
        event.title = params!!.eventTitle
        // First add in domain so we can do business logic
        // If this fails the event won't get added to the Repo.
        params!!.user.addEvent(event)
        event
    }

    data class Params(
        val eventTitle: String,
        var event: Event,
        val user: User
    )
}
