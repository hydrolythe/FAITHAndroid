package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

const val TEXT_FILENAME = "text"

class SaveEventTextUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventTextUseCase.SaveTextParams>(observeScheduler) {

    override fun buildUseCaseObservable(params: SaveEventTextUseCase.SaveTextParams): Completable {
        return Completable.fromSingle(
            storageRepository.writeHTML(params.text, params.event, TEXT_FILENAME)
                .doOnSuccess { storedFile ->
                    params.event.addNewTextDetail(storedFile, TEXT_FILENAME)
                })
    }

    class SaveTextParams(
        val event: Event,
        val text: String
    )
}
