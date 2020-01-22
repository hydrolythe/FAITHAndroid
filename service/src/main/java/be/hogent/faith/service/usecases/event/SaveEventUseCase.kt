package be.hogent.faith.service.usecases.event

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

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return Completable.fromCallable {
            setEventTitle(params.event)
            addEventToUser(params.event)
            try {
                // TODO: is een completable, dus moet op subscribed worden
                eventRepository.insert(params.event, params.user)
            } catch (e: Exception) {
                // Rollback
                removeEventFromUser(params.event)
                throw(e)
            }
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
