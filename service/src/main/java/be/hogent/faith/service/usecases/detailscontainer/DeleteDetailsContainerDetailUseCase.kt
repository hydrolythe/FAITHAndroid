package be.hogent.faith.service.usecases.detailscontainer

import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.DetailContainerRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

open class DeleteDetailsContainerDetailUseCase<T : DetailsContainer>(
    val detailsContainerRepository: DetailContainerRepository<T>,
    observeScheduler: Scheduler
) : CompletableUseCase<DeleteDetailsContainerDetailUseCase.Params>(
    observeScheduler
) {
    override fun buildUseCaseObservable(params: Params): Completable {
        return detailsContainerRepository.deleteDetail(params.detail)
    }

    data class Params(val detail: Detail)
}
