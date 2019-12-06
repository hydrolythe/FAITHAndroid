package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.localStorage.ITemporaryStorage
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveEventAudioDetailUseCase(
    private val tempStorageRepo: ITemporaryStorage,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventAudioDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseObservable(params: Params): Completable {
        return tempStorageRepo.storeDetailWithEvent(
            params.audioDetail,
            params.event
        ).doOnComplete {
            params.event.addDetail(params.audioDetail)
        }
    }

    data class Params(
        val audioDetail: AudioDetail,
        val event: Event
    )
}
