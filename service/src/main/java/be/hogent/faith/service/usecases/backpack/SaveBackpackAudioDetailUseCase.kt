package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.backpack.IDummyStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackAudioDetailUseCase(
    private val dummyStorageRepo: IDummyStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackAudioDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return dummyStorageRepo.storeDetail(params.audioDetail)
    }

    data class Params(val audioDetail: AudioDetail)
}
