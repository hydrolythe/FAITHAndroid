package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

open class SaveEventUseCase(
    private val eventRepository: EventRepository,
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromCallable {
            params.event.title = params.eventTitle
            // First add in domain so we can do business logic
            // If this fails the event won't get added to the Repo.
            params.user.addEvent(params.event)
        }.andThen(
            // Gebruik ( bij andThen ipv { anders problemen.
            // Zie https://stackoverflow.com/questions/45680256/rxjava-completabe-andthen-testing
            Completable.fromMaybe(
                eventRepository.insert(params.event, params.user)
                    .doOnSuccess {
                        // remove the tempory files
                        params.event.emotionAvatar?.let { file -> storageRepository.deleteFile(file) }
                        params.event.details.forEach { detail -> storageRepository.deleteFile(detail.file) }
                        params.event = it
                    })
        )
    }

    data class Params(
        val eventTitle: String,
        var event: Event,
        val user: User
    )
}
