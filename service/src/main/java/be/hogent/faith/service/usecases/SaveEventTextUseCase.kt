package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventTextUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventTextUseCase.SaveTextParams>(observeScheduler) {

    override fun buildUseCaseObservable(params: SaveTextParams): Completable {
        return Completable.fromSingle(
            storageRepository.saveText(params.text, params.event)
                .doOnSuccess { storedFile ->
                    params.event.addNewTextDetail(storedFile, "textName")
                })
    }

    class SaveTextParams(
        val event: Event,
        val text: String
    )
}
