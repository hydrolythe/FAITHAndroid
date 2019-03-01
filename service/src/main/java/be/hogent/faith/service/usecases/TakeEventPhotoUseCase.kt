package be.hogent.faith.service.usecases

import be.hogent.faith.domain.models.Detail
import be.hogent.faith.domain.models.DetailType
import be.hogent.faith.domain.models.Event
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.service.usecases.interfaces.PhotoTaker
import be.hogent.faith.storage.StorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TakeEventPhotoUseCase(
    private val storageRepository: StorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<TakeEventPhotoUseCase.Params>(
    Schedulers.io(),
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        // TODO: possibly clean up to return a Completable that completes when the photoTaker completes
        val saveFile = storageRepository.createDetailSaveFile(params.event)
        return params.photoTaker.takePhoto(saveFile).doOnComplete {
            params.event.addDetail(Detail(DetailType.PICTURE, saveFile))
        }
    }

    data class Params(val photoTaker: PhotoTaker, val event: Event)
}