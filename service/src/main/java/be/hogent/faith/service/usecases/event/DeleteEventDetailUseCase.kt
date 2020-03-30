package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

open class DeleteEventDetailUseCase(
    private val eventRepository: EventRepository,
    private val storageRepository: IStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<DeleteEventDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.mergeArray(
            storageRepository.deleteDetail(params.detail, params.event),
            eventRepository.deleteDetail(params.detail, params.event)
        ).andThen(
            removeDetailFromEvent(params.detail, params.event)
        )
    }

    private fun removeDetailFromEvent(detail: Detail, event: Event): Completable {
        return Completable.fromCallable {
            event.removeDetail(detail)
        }
    }

    data class Params(
        val detail: Detail,
        val event: Event
    )
}
