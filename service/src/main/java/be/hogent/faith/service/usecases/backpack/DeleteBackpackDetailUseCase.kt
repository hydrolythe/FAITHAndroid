package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.IBackpackRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class DeleteBackpackDetailUseCase(
    private val backpackRepository: IBackpackRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<DeleteBackpackDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return backpackRepository.deleteDetail(params.detail)
    }

    data class Params(val detail: Detail)
}
