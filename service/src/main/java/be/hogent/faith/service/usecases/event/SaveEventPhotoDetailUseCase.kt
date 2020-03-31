package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.localstorage.ITemporaryStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventPhotoDetailUseCase(
    private val tempStorageRepo: ITemporaryStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventPhotoDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return tempStorageRepo.storeDetailWithEvent(params.photoDetail, params.event)
            .doOnComplete {
                params.event.addDetail(params.photoDetail)
            }
    }

    data class Params(
        val photoDetail: PhotoDetail,
        val event: Event
    )
}