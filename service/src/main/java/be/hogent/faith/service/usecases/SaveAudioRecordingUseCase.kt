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
) : CompletableUseCase<SaveAudioRecordingUseCase.SaveAudioRecordingParams>(observeScheduler) {

    override fun buildUseCaseObservable(params: SaveAudioRecordingParams): Completable {
        return Completable.fromSingle(
            storageRepository.storeAudioRecording(params.tempStorageFile, params.event)
                .doOnSuccess { storedFile ->
                    params.event.addNewAudioDetail(storedFile, params.recordingName)
                })
    }

    class SaveAudioRecordingParams(
        val tempStorageFile: File,
        val event: Event,
        val recordingName: String
    )
}
