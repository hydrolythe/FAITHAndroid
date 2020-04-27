package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.encryption.IEventEncryptionService
import be.hogent.faith.service.repositories.IEventRepository
import be.hogent.faith.service.repositories.IFileStorageRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

open class SaveEventUseCase(
    private val eventEncryptionService: IEventEncryptionService,
    private val filesStorageRepository: IFileStorageRepository,
    private val eventRepository: IEventRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observeScheduler) {

    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return eventEncryptionService.encrypt(params.event)
            .flatMap(filesStorageRepository::saveEventFiles)
            .flatMapCompletable(eventRepository::insert)
            .andThen {
                with(params) {
                    user.addEvent(event)
                }
            }
    }

    data class Params(
        var event: Event,
        val user: User
    )
}
