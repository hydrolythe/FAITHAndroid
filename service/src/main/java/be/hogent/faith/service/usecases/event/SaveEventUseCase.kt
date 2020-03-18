package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.IEventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

open class SaveEventUseCase(
    private val eventRepository: IEventRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observeScheduler) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return Completable.fromCallable {
            setEventTitle(params.event)
            addEventToUser(params.event)
        }.andThen(
            eventRepository.insert(params.event, params.user)
        ).doOnError {
            // TODO: also clean up in repo?
            removeEventFromUser(params.event)
        }
    }

    private fun addEventToUser(event: Event) {
        params!!.user.addEvent(event)
    }

    private fun removeEventFromUser(event: Event) {
        params!!.user.removeEvent(event)
    }

    private fun setEventTitle(event: Event) {
        event.title = params!!.eventTitle
    }

    data class Params(
        val eventTitle: String,
        var event: Event,
        val user: User
    )
}
