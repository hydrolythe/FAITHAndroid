package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import java.io.File

class SaveEventAudioUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventAudioUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.saveEventAudio(
                params.tempStorageFile,
                params.event
            ).doOnSuccess { storedFile ->
                // TODO: remove once detail names are removed
                params.event.addNewAudioDetail(storedFile)
            })
    }

    data class Params(
        val tempStorageFile: File,
        val event: Event
    )
}
