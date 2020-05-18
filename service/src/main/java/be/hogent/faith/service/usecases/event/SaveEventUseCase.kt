package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import timber.log.Timber

open class SaveEventUseCase(
    private val eventEncryptionService: IEventEncryptionService,
    private val filesStorageRepository: IFileStorageRepository,
    private val eventRepository: IEventRepository,
    observer: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observer) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return eventEncryptionService.encrypt(params.event)
            .doOnSuccess { Timber.i("encrypted event ${params.event.uuid}") }
            .flatMap(filesStorageRepository::saveEventFiles)
            .doOnSuccess { Timber.i("stored files for event ${params.event.uuid}") }
            .flatMapCompletable(eventRepository::insert)
            .doOnComplete { Timber.i("stored data for event ${params.event.uuid}") }
            .andThen(Completable.fromAction {
                with(params) {
                    user.addEvent(event)
                    Timber.i("added event to user")
                }
            })
    }

    data class Params(
        var event: Event,
        val user: User
    )
}
