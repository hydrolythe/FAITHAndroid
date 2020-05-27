package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class DeleteEventUseCase(
    private val eventRepository: IEventRepository,
    private val fileStorageRepository: IFileStorageRepository,
    observer: Scheduler
) : CompletableUseCase<DeleteEventUseCase.Params>(observer) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return fileStorageRepository.deleteEventFiles(params.event)
            .subscribeOn(subscriber)
            .andThen(eventRepository.delete(params.event, params.user)
                .subscribeOn(subscriber))
            .andThen(Completable.fromAction {
                params.user.removeEvent(params.event)
            })

    }

    data class Params(
        val event: Event,
        val user: User
    )
}
