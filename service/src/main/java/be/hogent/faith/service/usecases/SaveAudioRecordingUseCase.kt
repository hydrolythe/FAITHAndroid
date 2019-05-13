package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import java.io.File

class SaveAudioRecordingUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveAudioRecordingUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.fromSingle(
            storageRepository.storeAudioRecording(params.tempStorageFile, params.event)
                .doOnSuccess { storedFile ->
                    // TODO: remove once detail names are removed
                    params.event.addNewAudioDetail(storedFile, "audioRecordingName")
                })
    }

    data class Params(
        val tempStorageFile: File,
        val event: Event
    )
}
