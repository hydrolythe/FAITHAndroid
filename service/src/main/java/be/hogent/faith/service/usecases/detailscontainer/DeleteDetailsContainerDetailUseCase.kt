package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

class DeleteDetailsContainerDetailUseCase<T : DetailsContainer>(
    private val backpackRepository: IDetailContainerRepository<T>,
    observeScheduler: Scheduler
) : CompletableUseCase<DeleteDetailsContainerDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return backpackRepository.deleteDetail(params.detail)
    }

    data class Params(val detail: Detail)
}
