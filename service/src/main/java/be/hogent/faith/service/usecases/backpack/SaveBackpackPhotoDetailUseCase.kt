package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackPhotoDetailUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
): CompletableUseCase<SaveBackpackPhotoDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.storeDetail(params.photoDetail)
    }

    data class Params(val photoDetail: PhotoDetail)
}