package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class SaveBackpackExternalVideoDetailUseCase(
    private val backpackRepository: BackpackRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveBackpackExternalVideoDetailUseCase.Params>(
        observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return Completable.complete()
    }

    data class Params(val externalVideoDetail: ExternalVideoDetail)
}