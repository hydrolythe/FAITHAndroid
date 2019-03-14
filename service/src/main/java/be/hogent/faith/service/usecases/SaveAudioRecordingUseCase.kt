package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.io.File

class SaveAudioRecordingUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveAudioRecordingUseCase.SaveAudioRecordingParams>(Schedulers.io(), observeScheduler) {

    override fun buildUseCaseObservable(params: SaveAudioRecordingUseCase.SaveAudioRecordingParams): Completable {
        return Completable.fromSingle(
            storageRepository.storeAudioRecording(params.tempStorageFile, params.event)
                .doOnSuccess { storedFile ->
                    //TODO: also save name once this property is added to Detail
                    params.event.addDetail(Detail(DetailType.AUDIO, storedFile))
                })
    }

    class SaveAudioRecordingParams(val tempStorageFile: File, val event: Event, val name: String)
}
